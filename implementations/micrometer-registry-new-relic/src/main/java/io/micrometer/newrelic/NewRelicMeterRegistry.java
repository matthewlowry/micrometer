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
package io.micrometer.newrelic;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MissingRequiredConfigurationException;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.*;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.core.ipc.http.HttpUrlConnectionSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micrometer.core.instrument.util.StringEscapeUtils.escapeJson;

/**
 * Publishes metrics to New Relic Insights.
 *
 * @author Jon Schneider
 * @author Johnny Lim
 * @since 1.0.0
 */
public class NewRelicMeterRegistry extends StepMeterRegistry {
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new NamedThreadFactory("new-relic-metrics-publisher");
    private final NewRelicConfig config;
    private final HttpSender httpClient;
    private final Logger logger = LoggerFactory.getLogger(NewRelicMeterRegistry.class);

    /**
     * @param config Configuration options for the registry that are describable as properties.
     * @param clock  The clock to use for timings.
     */
    @SuppressWarnings("deprecation")
    public NewRelicMeterRegistry(NewRelicConfig config, Clock clock) {
        this(config, clock, DEFAULT_THREAD_FACTORY,
                new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout()));
    }

    /**
     * @param config        Configuration options for the registry that are describable as properties.
     * @param clock         The clock to use for timings.
     * @param threadFactory The thread factory to use to create the publishing thread.
     * @deprecated Use {@link #builder(NewRelicConfig)} instead.
     */
    @Deprecated
    public NewRelicMeterRegistry(NewRelicConfig config, Clock clock, ThreadFactory threadFactory) {
        this(config, clock, threadFactory, new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout()));
    }

    // VisibleForTesting
    NewRelicMeterRegistry(NewRelicConfig config, Clock clock, ThreadFactory threadFactory, HttpSender httpClient) {
        super(config, clock);

        if (!config.meterNameEventTypeEnabled() && StringUtils.isEmpty(config.eventType())) {
            throw new MissingRequiredConfigurationException("eventType must be set to report metrics to New Relic");
        }
        if (StringUtils.isEmpty(config.accountId())) {
            throw new MissingRequiredConfigurationException("accountId must be set to report metrics to New Relic");
        }
        if (StringUtils.isEmpty(config.apiKey())) {
            throw new MissingRequiredConfigurationException("apiKey must be set to report metrics to New Relic");
        }
        if (StringUtils.isEmpty(config.uri())) {
            throw new MissingRequiredConfigurationException("uri must be set to report metrics to New Relic");
        }

        this.config = config;
        this.httpClient = httpClient;

        config().namingConvention(new NewRelicNamingConvention());
        start(threadFactory);
    }

    public static Builder builder(NewRelicConfig config) {
        return new Builder(config);
    }

    @Override
    public void start(ThreadFactory threadFactory) {
        if (config.enabled()) {
            logger.info("publishing metrics to new relic every " + TimeUtils.format(config.step()));
        }
        super.start(threadFactory);
    }

    @Override
    protected void publish() {
        String insightsEndpoint = config.uri() + "/v1/accounts/" + config.accountId() + "/events";

        // New Relic's Insights API limits us to 1000 events per call
        // 1:1 mapping between Micrometer meters and New Relic events
        for (List<Meter> batch : MeterPartition.partition(this, Math.min(config.batchSize(), 1000))) {
            sendEvents(insightsEndpoint, batch.stream().flatMap(meter -> meter.match(
                    this::writeGauge,
                    this::writeCounter,
                    this::writeTimer,
                    this::writeSummary,
                    this::writeLongTaskTimer,
                    this::writeTimeGauge,
                    this::writeFunctionCounter,
                    this::writeFunctionTimer,
                    this::writeMeter)));
        }
    }

    private Stream<String> writeLongTaskTimer(LongTaskTimer ltt) {
        return Stream.of(
                event(ltt.getId(),
                        new Attribute("activeTasks", ltt.activeTasks()),
                        new Attribute("duration", ltt.duration(getBaseTimeUnit())),
                        new Attribute("timeUnit", getBaseTimeUnit().name().toLowerCase()))
        );
    }

    // VisibleForTesting
    Stream<String> writeFunctionCounter(FunctionCounter counter) {
        double count = counter.count();
        if (Double.isFinite(count)) {
            return Stream.of(event(counter.getId(), new Attribute("throughput", count)));
        }
        return Stream.empty();
    }

    private Stream<String> writeCounter(Counter counter) {
        return Stream.of(event(counter.getId(), new Attribute("throughput", counter.count())));
    }

    // VisibleForTesting
    Stream<String> writeGauge(Gauge gauge) {
        double value = gauge.value();
        if (Double.isFinite(value)) {
            return Stream.of(event(gauge.getId(), new Attribute("value", value)));
        }
        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeTimeGauge(TimeGauge gauge) {
        double value = gauge.value(getBaseTimeUnit());
        if (Double.isFinite(value)) {
            return Stream.of(
                    event(gauge.getId(), 
                            new Attribute("value", value),
                            new Attribute("timeUnit", getBaseTimeUnit().name().toLowerCase())));
        }
        return Stream.empty();
    }

    private Stream<String> writeSummary(DistributionSummary summary) {
        return Stream.of(
                event(summary.getId(),
                        new Attribute("count", summary.count()),
                        new Attribute("avg", summary.mean()),
                        new Attribute("total", summary.totalAmount()),
                        new Attribute("max", summary.max())
                )
        );
    }

    private Stream<String> writeTimer(Timer timer) {
        return Stream.of(event(timer.getId(),
                new Attribute("count", timer.count()),
                new Attribute("avg", timer.mean(getBaseTimeUnit())),
                new Attribute("totalTime", timer.totalTime(getBaseTimeUnit())),
                new Attribute("max", timer.max(getBaseTimeUnit())),
                new Attribute("timeUnit", getBaseTimeUnit().name().toLowerCase())
        ));
    }

    private Stream<String> writeFunctionTimer(FunctionTimer timer) {
        return Stream.of(
                event(timer.getId(),
                        new Attribute("count", timer.count()),
                        new Attribute("avg", timer.mean(getBaseTimeUnit())),
                        new Attribute("totalTime", timer.totalTime(getBaseTimeUnit())),
                        new Attribute("timeUnit", getBaseTimeUnit().name().toLowerCase())
                )
        );
    }

    // VisibleForTesting
    Stream<String> writeMeter(Meter meter) {
        // Snapshot values should be used throughout this method as there are chances for values to be changed in-between.
        Map<String, Attribute> attributes = new HashMap<>();
        for (Measurement measurement : meter.measure()) {
            double value = measurement.getValue();
            if (!Double.isFinite(value)) {
                continue;
            }
            String name = measurement.getStatistic().getTagValueRepresentation();
            attributes.put(name, new Attribute(name, value));
        }
        if (attributes.isEmpty()) {
            return Stream.empty();
        }
        return Stream.of(event(meter.getId(), attributes.values().toArray(new Attribute[0])));
    }

    private String event(Meter.Id id, Attribute... attributes) {
        if (!config.meterNameEventTypeEnabled()) {
            // Include contextual attributes when publishing all metrics under a single categorical eventType,
            // NOT when publishing an eventType per Meter/metric name
            int size = attributes.length;
            Attribute[] newAttrs = Arrays.copyOf(attributes, size + 2);

            String name = id.getConventionName(config().namingConvention());
            newAttrs[size] = new Attribute("metricName", name);
            newAttrs[size + 1] = new Attribute("metricType", id.getType().toString());

            return event(id, Tags.empty(), newAttrs);
        }
        return event(id, Tags.empty(), attributes);
    }

    private String event(Meter.Id id, Iterable<Tag> extraTags, Attribute... attributes) {
        StringBuilder tagsJson = new StringBuilder();

        for (Tag tag : getConventionTags(id)) {
            tagsJson.append(",\"").append(escapeJson(tag.getKey())).append("\":\"").append(escapeJson(tag.getValue())).append("\"");
        }

        NamingConvention convention = config().namingConvention();
        for (Tag tag : extraTags) {
            tagsJson.append(",\"").append(escapeJson(convention.tagKey(tag.getKey())))
                    .append("\":\"").append(escapeJson(convention.tagValue(tag.getValue()))).append("\"");
        }

        String eventType = getEventType(id);
        
        return Arrays.stream(attributes)
                .map(attr ->
                        (attr.getValue() instanceof Number)
                            ? ",\"" + attr.getName() + "\":" + DoubleFormat.wholeOrDecimal(((Number) attr.getValue()).doubleValue())
                            : ",\"" + attr.getName() + "\":\"" + convention.tagValue(attr.getValue().toString()) + "\""
                )
                .collect(Collectors.joining("", "{\"eventType\":\"" + escapeJson(eventType) + "\"", tagsJson + "}"));
    }

    private String getEventType(Meter.Id id) {
        if (config.meterNameEventTypeEnabled()) {
            return id.getConventionName(config().namingConvention());
        } else {
            return config.eventType();
        }
    }
    
    private void sendEvents(String insightsEndpoint, Stream<String> events) {
        try {
            AtomicInteger totalEvents = new AtomicInteger();

            httpClient.post(insightsEndpoint)
                    .withHeader("X-Insert-Key", config.apiKey())
                    .withJsonContent(events.peek(ev -> totalEvents.incrementAndGet()).collect(Collectors.joining(",", "[", "]")))
                    .send()
                    .onSuccess(response -> logger.debug("successfully sent {} metrics to New Relic.", totalEvents))
                    .onError(response -> logger.error("failed to send metrics to new relic: http {} {}", response.code(), response.body()));
        } catch (Throwable e) {
            logger.warn("failed to send metrics to new relic", e);
        }
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    public static class Builder {
        private final NewRelicConfig config;

        private Clock clock = Clock.SYSTEM;
        private ThreadFactory threadFactory = DEFAULT_THREAD_FACTORY;
        private HttpSender httpClient;

        @SuppressWarnings("deprecation")
        Builder(NewRelicConfig config) {
            this.config = config;
            this.httpClient = new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout());
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder httpClient(HttpSender httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public NewRelicMeterRegistry build() {
            return new NewRelicMeterRegistry(config, clock, threadFactory, httpClient);
        }
    }

    private class Attribute {
        private final String name;
        private final Object value;

        private Attribute(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
