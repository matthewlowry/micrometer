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
package io.micrometer.spring.autoconfigure.export.datadog;

import io.micrometer.datadog.DatadogConfig;
import io.micrometer.spring.autoconfigure.export.properties.StepRegistryPropertiesConfigAdapter;

/**
 * Adapter to convert {@link DatadogProperties} to a {@link DatadogConfig}.
 *
 * @author Jon Schneider
 * @author Phillip Webb
 */
class DatadogPropertiesConfigAdapter extends StepRegistryPropertiesConfigAdapter<DatadogProperties> implements DatadogConfig {

    DatadogPropertiesConfigAdapter(DatadogProperties properties) {
        super(properties);
    }

    @Override
    public String apiKey() {
        return get(DatadogProperties::getApiKey, DatadogConfig.super::apiKey);
    }

    @Override
    public String applicationKey() {
        return get(DatadogProperties::getApplicationKey, DatadogConfig.super::applicationKey);
    }

    @Override
    public String hostTag() {
        return get(DatadogProperties::getHostTag, DatadogConfig.super::hostTag);
    }

    @Override
    public String uri() {
        return get(DatadogProperties::getUri, DatadogConfig.super::uri);
    }

    @Override
    public boolean descriptions() {
        return get(DatadogProperties::isDescriptions, DatadogConfig.super::descriptions);
    }

}
