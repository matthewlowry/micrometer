/**
 * Copyright 2019 VMware, Inc.
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
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.connection.ServerId;
import com.mongodb.event.*;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.NonNullFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link ConnectionPoolListener} for collecting connection pool metrics from {@link MongoClient}.
 *
 * @author Christophe Bornet
 * @since 1.2.0
 */
@NonNullApi
@NonNullFields
@Incubating(since = "1.2.0")
public class MongoMetricsConnectionPoolListener extends ConnectionPoolListenerAdapter {

    private static final String METRIC_PREFIX = "mongodb.driver.pool.";

    private final Map<ServerId, AtomicInteger> poolSize = new ConcurrentHashMap<>();
    private final Map<ServerId, AtomicInteger> checkedOutCount = new ConcurrentHashMap<>();
    private final Map<ServerId, AtomicInteger> waitQueueSize = new ConcurrentHashMap<>();
    private final Map<ServerId, List<Meter>> meters = new ConcurrentHashMap<>();

    private final MeterRegistry registry;

    public MongoMetricsConnectionPoolListener(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void connectionPoolOpened(ConnectionPoolOpenedEvent event) {
        List<Meter> connectionMeters = new ArrayList<>();
        connectionMeters.add(registerGauge(event.getServerId(), METRIC_PREFIX + "size",
                "the current size of the connection pool, including idle and and in-use members", poolSize));
        connectionMeters.add(registerGauge(event.getServerId(), METRIC_PREFIX + "checkedout",
                "the count of connections that are currently in use", checkedOutCount));
        connectionMeters.add(registerGauge(event.getServerId(), METRIC_PREFIX + "waitqueuesize",
                "the current size of the wait queue for a connection from the pool", waitQueueSize));
        meters.put(event.getServerId(), connectionMeters);
    }

    @Override
    public void connectionPoolClosed(ConnectionPoolClosedEvent event) {
        ServerId serverId = event.getServerId();
        for (Meter meter : meters.get(serverId)) {
            registry.remove(meter);
        }
        meters.remove(serverId);
        poolSize.remove(serverId);
        checkedOutCount.remove(serverId);
        waitQueueSize.remove(serverId);
    }

    @Override
    public void connectionCheckedOut(ConnectionCheckedOutEvent event) {
        AtomicInteger gauge = checkedOutCount.get(event.getConnectionId().getServerId());
        if (gauge != null) {
            gauge.incrementAndGet();
        }
    }

    @Override
    public void connectionCheckedIn(ConnectionCheckedInEvent event) {
        AtomicInteger gauge = checkedOutCount.get(event.getConnectionId().getServerId());
        if (gauge != null) {
            gauge.decrementAndGet();
        }
    }

    @Override
    public void waitQueueEntered(ConnectionPoolWaitQueueEnteredEvent event) {
        AtomicInteger gauge = waitQueueSize.get(event.getServerId());
        if (gauge != null) {
            gauge.incrementAndGet();
        }
    }

    @Override
    public void waitQueueExited(ConnectionPoolWaitQueueExitedEvent event) {
        AtomicInteger gauge = waitQueueSize.get(event.getServerId());
        if (gauge != null) {
            gauge.decrementAndGet();
        }
    }

    @Override
    public void connectionAdded(ConnectionAddedEvent event) {
        AtomicInteger gauge = poolSize.get(event.getConnectionId().getServerId());
        if (gauge != null) {
            gauge.incrementAndGet();
        }
    }

    @Override
    public void connectionRemoved(ConnectionRemovedEvent event) {
        AtomicInteger gauge = poolSize.get(event.getConnectionId().getServerId());
        if (gauge != null) {
            gauge.decrementAndGet();
        }
    }

    private Gauge registerGauge(ServerId serverId, String metricName, String description, Map<ServerId, AtomicInteger> metrics) {
        AtomicInteger value = new AtomicInteger();
        metrics.put(serverId, value);
        return Gauge.builder(metricName, value, AtomicInteger::doubleValue)
                    .description(description)
                    .tag("cluster.id", serverId.getClusterId().getValue())
                    .tag("server.address", serverId.getAddress().toString())
                    .register(registry);
    }

}
