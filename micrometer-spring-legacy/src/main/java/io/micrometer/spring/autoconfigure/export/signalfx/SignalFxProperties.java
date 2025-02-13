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
package io.micrometer.spring.autoconfigure.export.signalfx;

import java.time.Duration;

import io.micrometer.spring.autoconfigure.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring metrics export to SignalFX.
 *
 * @author Jon Schneider
 */
@ConfigurationProperties(prefix = "management.metrics.export.signalfx")
public class SignalFxProperties extends StepRegistryProperties {

    /**
     * Step size (i.e. reporting frequency) to use.
     */
    private Duration step = Duration.ofSeconds(10);

    /**
     * SignalFX access token.
     */
    private String accessToken;

    /**
     * URI to ship metrics to.
     */
    private String uri = "https://ingest.signalfx.com";

    /**
     * Uniquely identifies the app instance that is publishing metrics to SignalFx.
     * Defaults to the local host name.
     */
    private String source;

    @Override
    public Duration getStep() {
        return this.step;
    }

    @Override
    public void setStep(Duration step) {
        this.step = step;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
