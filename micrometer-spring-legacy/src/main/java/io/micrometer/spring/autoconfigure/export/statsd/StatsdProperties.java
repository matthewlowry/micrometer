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
package io.micrometer.spring.autoconfigure.export.statsd;

import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * {@link ConfigurationProperties} for configuring StatsD metrics export.
 *
 * @author Jon Schneider
 * @author Stephane Nicoll
 */
@ConfigurationProperties(prefix = "management.metrics.export.statsd")
public class StatsdProperties {

    /**
     * Whether exporting of metrics to StatsD is enabled.
     */
    private boolean enabled = true;

    /**
     * StatsD line protocol to use.
     */
    private StatsdFlavor flavor = StatsdFlavor.DATADOG;

    /**
     * Host of the StatsD server to receive exported metrics.
     */
    private String host = "localhost";

    /**
     * Port of the StatsD server to receive exported metrics.
     */
    private Integer port = 8125;

    /**
     * Protocol of the StatsD server to receive exported metrics.
     */
    private StatsdProtocol protocol = StatsdProtocol.UDP;

    /**
     * Total length of a single payload should be kept within your network's MTU.
     */
    private Integer maxPacketLength = 1400;

    /**
     * How often gauges will be polled. When a gauge is polled, its value is recalculated
     * and if the value has changed (or publishUnchangedMeters is true), it is sent to the
     * StatsD server.
     */
    private Duration pollingFrequency = Duration.ofSeconds(10);

    /**
     * Whether to send unchanged meters to the StatsD server.
     */
    private boolean publishUnchangedMeters = true;

    /** Whether measurements should be buffered before sending to the StatsD server. */
    private boolean buffered = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public StatsdFlavor getFlavor() {
        return this.flavor;
    }

    public void setFlavor(StatsdFlavor flavor) {
        this.flavor = flavor;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public StatsdProtocol getProtocol() {
        return this.protocol;
    }

    public void setProtocol(StatsdProtocol protocol) {
        this.protocol = protocol;
    }

    public Integer getMaxPacketLength() {
        return this.maxPacketLength;
    }

    public void setMaxPacketLength(Integer maxPacketLength) {
        this.maxPacketLength = maxPacketLength;
    }

    public Duration getPollingFrequency() {
        return this.pollingFrequency;
    }

    public void setPollingFrequency(Duration pollingFrequency) {
        this.pollingFrequency = pollingFrequency;
    }

    public boolean isPublishUnchangedMeters() {
        return this.publishUnchangedMeters;
    }

    public void setPublishUnchangedMeters(boolean publishUnchangedMeters) {
        this.publishUnchangedMeters = publishUnchangedMeters;
    }

    public boolean isBuffered() {
        return buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }
}
