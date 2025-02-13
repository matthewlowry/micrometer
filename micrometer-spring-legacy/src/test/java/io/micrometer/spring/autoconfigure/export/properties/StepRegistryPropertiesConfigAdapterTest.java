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

import java.time.Duration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base test for {@link StepRegistryPropertiesConfigAdapter} implementations.
 *
 * @param <P> properties used by the tests
 * @param <A> adapter used by the tests
 * @author Stephane Nicoll
 */
public abstract class StepRegistryPropertiesConfigAdapterTest<P extends StepRegistryProperties, A extends StepRegistryPropertiesConfigAdapter<P>> {

    protected abstract P createProperties();

    protected abstract A createConfigAdapter(P properties);

    @Test
    public void whenPropertiesStepIsSetAdapterStepReturnsIt() {
        P properties = createProperties();
        properties.setStep(Duration.ofSeconds(42));
        assertThat(createConfigAdapter(properties).step())
                .isEqualTo(Duration.ofSeconds(42));
    }

    @Test
    public void whenPropertiesEnabledIsSetAdapterEnabledReturnsIt() {
        P properties = createProperties();
        properties.setEnabled(false);
        assertThat(createConfigAdapter(properties).enabled()).isFalse();
    }

    @Test
    public void whenPropertiesConnectTimeoutIsSetAdapterConnectTimeoutReturnsIt() {
        P properties = createProperties();
        properties.setConnectTimeout(Duration.ofMinutes(42));
        assertThat(createConfigAdapter(properties).connectTimeout())
                .isEqualTo(Duration.ofMinutes(42));
    }

    @Test
    public void whenPropertiesReadTimeoutIsSetAdapterReadTimeoutReturnsIt() {
        P properties = createProperties();
        properties.setReadTimeout(Duration.ofMillis(42));
        assertThat(createConfigAdapter(properties).readTimeout())
                .isEqualTo(Duration.ofMillis(42));
    }

    @Test
    public void whenPropertiesNumThreadsIsSetAdapterNumThreadsReturnsIt() {
        P properties = createProperties();
        properties.setNumThreads(42);
        assertThat(createConfigAdapter(properties).numThreads()).isEqualTo(42);
    }

    @Test
    public void whenPropertiesBatchSizeIsSetAdapterBatchSizeReturnsIt() {
        P properties = createProperties();
        properties.setBatchSize(10042);
        assertThat(createConfigAdapter(properties).batchSize()).isEqualTo(10042);
    }

}
