/**
 * Copyright 2018 VMware, Inc.
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
package io.micrometer.stackdriver;

import io.micrometer.core.instrument.config.MissingRequiredConfigurationException;
import io.micrometer.core.instrument.step.StepRegistryConfig;

/**
 * {@link StepRegistryConfig} for Stackdriver.
 *
 * @author Jon Schneider
 * @since 1.1.0
 */
public interface StackdriverConfig extends StepRegistryConfig {

    @Override
    default String prefix() {
        return "stackdriver";
    }

    default String projectId() {
        String v = get(prefix() + ".projectId");
        if (v == null)
            throw new MissingRequiredConfigurationException("projectId must be set to report metrics to Stackdriver");
        return v;
    }

    default String resourceType() {
        String resourceType = get(prefix() + ".resourceType");
        return resourceType == null ? "global" : resourceType;
    }
}
