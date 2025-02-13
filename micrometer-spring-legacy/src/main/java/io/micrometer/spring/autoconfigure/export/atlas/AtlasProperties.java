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
package io.micrometer.spring.autoconfigure.export.atlas;

import io.micrometer.spring.autoconfigure.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * {@link ConfigurationProperties} for configuring Atlas metrics export.
 *
 * @author Jon Schneider
 */
@ConfigurationProperties(prefix = "management.metrics.export.atlas")
public class AtlasProperties extends StepRegistryProperties {

    /**
     * URI of the Atlas server.
     */
    private String uri = "http://localhost:7101/api/v1/publish";

    /**
     * Time to live for meters that do not have any activity. After this period the meter
     * will be considered expired and will not get reported.
     */
    private Duration meterTimeToLive = Duration.ofMinutes(15);

    /**
     * Whether to enable streaming to Atlas LWC.
     */
    private boolean lwcEnabled;

    /**
     * Frequency for refreshing config settings from the LWC service.
     */
    private Duration configRefreshFrequency = Duration.ofSeconds(10);

    /**
     * Time to live for subscriptions from the LWC service.
     */
    private Duration configTimeToLive = Duration.ofSeconds(150);

    /**
     * URI for the Atlas LWC endpoint to retrieve current subscriptions.
     */
    private String configUri = "http://localhost:7101/lwc/api/v1/expressions/local-dev";

    /**
     * URI for the Atlas LWC endpoint to evaluate the data for a subscription.
     */
    private String evalUri = "http://localhost:7101/lwc/api/v1/evaluate";

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Duration getMeterTimeToLive() {
        return this.meterTimeToLive;
    }

    public void setMeterTimeToLive(Duration meterTimeToLive) {
        this.meterTimeToLive = meterTimeToLive;
    }

    public boolean isLwcEnabled() {
        return this.lwcEnabled;
    }

    public void setLwcEnabled(boolean lwcEnabled) {
        this.lwcEnabled = lwcEnabled;
    }

    public Duration getConfigRefreshFrequency() {
        return this.configRefreshFrequency;
    }

    public void setConfigRefreshFrequency(Duration configRefreshFrequency) {
        this.configRefreshFrequency = configRefreshFrequency;
    }

    public Duration getConfigTimeToLive() {
        return this.configTimeToLive;
    }

    public void setConfigTimeToLive(Duration configTimeToLive) {
        this.configTimeToLive = configTimeToLive;
    }

    public String getConfigUri() {
        return this.configUri;
    }

    public void setConfigUri(String configUri) {
        this.configUri = configUri;
    }

    public String getEvalUri() {
        return this.evalUri;
    }

    public void setEvalUri(String evalUri) {
        this.evalUri = evalUri;
    }

}
