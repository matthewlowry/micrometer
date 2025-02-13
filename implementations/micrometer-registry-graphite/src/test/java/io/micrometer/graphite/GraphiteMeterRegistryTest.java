/**
 * Copyright 2017 VMware, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.graphite;

import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.lang.Nullable;
import io.netty.channel.ChannelOption;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

import java.net.DatagramSocket;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphiteMeterRegistryTest {
    /**
     * A port that is NOT the default for DogStatsD or Telegraf, so these unit tests
     * do not fail if one of those agents happens to be running on the same box.
     */
    private static final int PORT = findAvailableUdpPort();
    private MockClock mockClock = new MockClock();

    private static int findAvailableUdpPort() {
        for (int port = 1024; port <= 65535; port++) {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close();
                return port;
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("no available UDP port");
    }

    @Test
    void metricPrefixes() throws InterruptedException {
        final CountDownLatch receiveLatch = new CountDownLatch(1);

        final GraphiteMeterRegistry registry = new GraphiteMeterRegistry(new GraphiteConfig() {
            @Override
            @Nullable
            public String get(String key) {
                return null;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(1);
            }

            @Override
            public GraphiteProtocol protocol() {
                return GraphiteProtocol.UDP;
            }

            @Override
            public int port() {
                return PORT;
            }

            @Override
            public String[] tagsAsPrefix() {
                return new String[]{"application"};
            }
        }, mockClock);

        Connection server = UdpServer.create()
                .option(ChannelOption.SO_REUSEADDR, true)
                .host("localhost")
                .port(PORT)
                .handle((in, out) -> {
                    in.receive()
                            .asString()
                            .subscribe(line -> {
                                assertThat(line).startsWith("APPNAME.myTimer");
                                receiveLatch.countDown();
                            });
                    return Flux.never();
                })
                .bind()
                .doOnSuccess(v -> {
                    registry.timer("my.timer", "application", "APPNAME")
                            .record(1, TimeUnit.MILLISECONDS);
                    registry.close();
                })
                .block(Duration.ofSeconds(10));

        assertTrue(receiveLatch.await(10, TimeUnit.SECONDS), "line was received");
        server.dispose();
    }
}
