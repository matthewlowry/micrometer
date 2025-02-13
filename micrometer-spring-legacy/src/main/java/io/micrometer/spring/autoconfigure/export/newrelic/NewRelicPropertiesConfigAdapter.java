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
package io.micrometer.spring.autoconfigure.export.newrelic;

import io.micrometer.newrelic.NewRelicConfig;
import io.micrometer.spring.autoconfigure.export.properties.StepRegistryPropertiesConfigAdapter;

/**
 * Adapter to convert {@link NewRelicProperties} to a {@link NewRelicConfig}.
 *
 * @author Jon Schneider
 * @author Neil Powell
 */
class NewRelicPropertiesConfigAdapter extends StepRegistryPropertiesConfigAdapter<NewRelicProperties>
    implements NewRelicConfig {

    NewRelicPropertiesConfigAdapter(NewRelicProperties properties) {
        super(properties);
    }

    @Override
    public boolean meterNameEventTypeEnabled() {
        return get(NewRelicProperties::isMeterNameEventTypeEnabled, NewRelicConfig.super::meterNameEventTypeEnabled);
    }

    @Override
    public String eventType() {
        return get(NewRelicProperties::getEventType, NewRelicConfig.super::eventType);
    }

    @Override
    public String apiKey() {
        return get(NewRelicProperties::getApiKey, NewRelicConfig.super::apiKey);
    }

    @Override
    public String accountId() {
        return get(NewRelicProperties::getAccountId, NewRelicConfig.super::accountId);
    }

    @Override
    public String uri() {
        return get(NewRelicProperties::getUri, NewRelicConfig.super::uri);
    }
}
