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
package io.micrometer.spring.autoconfigure.export.wavefront;

import java.net.URI;
import java.time.Duration;

import io.micrometer.spring.autoconfigure.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring Wavefront metrics export.
 *
 * @author Jon Schneider
 * @since 1.0.0
 */
@ConfigurationProperties("management.metrics.export.wavefront")
public class WavefrontProperties extends StepRegistryProperties {

    /**
     * Step size (i.e. reporting frequency) to use.
     */
    private Duration step = Duration.ofSeconds(10);

    /**
     * URI to ship metrics to.
     */
    private URI uri = URI.create("https://longboard.wavefront.com");

    /**
     * Unique identifier for the app instance that is the source of metrics being
     * published to Wavefront. Defaults to the local host name.
     */
    private String source;

    /**
     * API token used when publishing metrics directly to the Wavefront API host.
     */
    private String apiToken;

    /**
     * Global prefix to separate metrics originating from this app's white box
     * instrumentation from those originating from other Wavefront integrations when
     * viewed in the Wavefront UI.
     */
    private String globalPrefix;

    /**
     * Report histogram distributions aggregated into minute intervals.
     */
    private boolean reportMinuteDistribution = true;

    /**
     * Report histogram distributions aggregated into hour intervals.
     */
    private boolean reportHourDistribution = false;

    /**
     * Report histogram distributions aggregated into day intervals.
     */
    private boolean reportDayDistribution = false;

    public URI getUri() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public Duration getStep() {
        return this.step;
    }

    @Override
    public void setStep(Duration step) {
        this.step = step;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getApiToken() {
        return this.apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getGlobalPrefix() {
        return this.globalPrefix;
    }

    public void setGlobalPrefix(String globalPrefix) {
        this.globalPrefix = globalPrefix;
    }

    public boolean isReportMinuteDistribution() {
        return reportMinuteDistribution;
    }

    public void setReportMinuteDistribution(boolean reportMinuteDistribution) {
        this.reportMinuteDistribution = reportMinuteDistribution;
    }

    public boolean isReportHourDistribution() {
        return reportHourDistribution;
    }

    public void setReportHourDistribution(boolean reportHourDistribution) {
        this.reportHourDistribution = reportHourDistribution;
    }

    public boolean isReportDayDistribution() {
        return reportDayDistribution;
    }

    public void setReportDayDistribution(boolean reportDayDistribution) {
        this.reportDayDistribution = reportDayDistribution;
    }
}
