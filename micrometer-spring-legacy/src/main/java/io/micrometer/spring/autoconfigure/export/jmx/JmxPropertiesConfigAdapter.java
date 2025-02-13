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
package io.micrometer.spring.autoconfigure.export.jmx;

import io.micrometer.jmx.JmxConfig;
import io.micrometer.spring.autoconfigure.export.properties.PropertiesConfigAdapter;

import java.time.Duration;

/**
 * Adapter to convert {@link JmxProperties} to a {@link JmxConfig}.
 *
 * @author Jon Schneider
 */
class JmxPropertiesConfigAdapter extends PropertiesConfigAdapter<JmxProperties> implements JmxConfig {

    JmxPropertiesConfigAdapter(JmxProperties properties) {
        super(properties);
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String domain() {
        return get(JmxProperties::getDomain, JmxConfig.super::domain);
    }

    @Override
    public Duration step() {
        return get(JmxProperties::getStep, JmxConfig.super::step);
    }
}
