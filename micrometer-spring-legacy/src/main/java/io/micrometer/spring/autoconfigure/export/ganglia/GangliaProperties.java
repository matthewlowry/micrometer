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
package io.micrometer.spring.autoconfigure.export.ganglia;

import info.ganglia.gmetric4j.gmetric.GMetric;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * {@link ConfigurationProperties} for configuring Ganglia metrics export.
 *
 * @author Jon Schneider
 */
@ConfigurationProperties(prefix = "management.metrics.export.ganglia")
public class GangliaProperties {

    /**
     * Whether exporting of metrics to Ganglia is enabled.
     */
    private boolean enabled = true;

    /**
     * Step size (i.e. reporting frequency) to use.
     */
    private Duration step = Duration.ofMinutes(1);

    /**
     * Base time unit used to report rates.
     */
    private TimeUnit rateUnits = TimeUnit.SECONDS;

    /**
     * Base time unit used to report durations.
     */
    private TimeUnit durationUnits = TimeUnit.MILLISECONDS;

    /**
     * Ganglia protocol version. Must be either 3.1 or 3.0.
     */
    private String protocolVersion = "3.1";

    /**
     * UDP addressing mode, either unicast or multicast.
     */
    private GMetric.UDPAddressingMode addressingMode = GMetric.UDPAddressingMode.MULTICAST;

    /**
     * Time to live for metrics on Ganglia. Set the multi-cast Time-To-Live to be one
     * greater than the number of hops (routers) between the hosts.
     */
    private Integer timeToLive = 1;

    /**
     * Host of the Ganglia server to receive exported metrics.
     */
    private String host = "localhost";

    /**
     * Port of the Ganglia server to receive exported metrics.
     */
    private Integer port = 8649;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getStep() {
        return this.step;
    }

    public void setStep(Duration step) {
        this.step = step;
    }

    public TimeUnit getRateUnits() {
        return this.rateUnits;
    }

    public void setRateUnits(TimeUnit rateUnits) {
        this.rateUnits = rateUnits;
    }

    public TimeUnit getDurationUnits() {
        return this.durationUnits;
    }

    public void setDurationUnits(TimeUnit durationUnits) {
        this.durationUnits = durationUnits;
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public GMetric.UDPAddressingMode getAddressingMode() {
        return this.addressingMode;
    }

    public void setAddressingMode(GMetric.UDPAddressingMode addressingMode) {
        this.addressingMode = addressingMode;
    }

    public Integer getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
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

}
