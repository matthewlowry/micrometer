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
package io.micrometer.spring.autoconfigure.export.properties;

import io.micrometer.core.instrument.step.StepRegistryConfig;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base tests for {@link StepRegistryProperties} implementation.
 *
 * @author Stephane Nicoll
 */
public abstract class StepRegistryPropertiesTest {

    @SuppressWarnings("deprecation")
    protected void assertStepRegistryDefaultValues(StepRegistryProperties properties,
            StepRegistryConfig config) {
        assertThat(properties.getStep()).isEqualTo(config.step());
        assertThat(properties.isEnabled()).isEqualTo(config.enabled());
        assertThat(properties.getConnectTimeout()).isEqualTo(config.connectTimeout());
        assertThat(properties.getReadTimeout()).isEqualTo(config.readTimeout());
        assertThat(properties.getNumThreads()).isEqualTo(config.numThreads());
        assertThat(properties.getBatchSize()).isEqualTo(config.batchSize());
    }

    @Test
    public abstract void defaultValuesAreConsistent();

}
