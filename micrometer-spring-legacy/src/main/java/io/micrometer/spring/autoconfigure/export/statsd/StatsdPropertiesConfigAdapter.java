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
package io.micrometer.spring.autoconfigure.export.statsd;

import io.micrometer.spring.autoconfigure.export.properties.PropertiesConfigAdapter;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdProtocol;

import java.time.Duration;

/**
 * Adapter to convert {@link StatsdProperties} to a {@link StatsdConfig}.
 *
 * @author Jon Schneider
 */
class StatsdPropertiesConfigAdapter extends PropertiesConfigAdapter<StatsdProperties> implements StatsdConfig {

    StatsdPropertiesConfigAdapter(StatsdProperties properties) {
        super(properties);
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public StatsdFlavor flavor() {
        return get(StatsdProperties::getFlavor, StatsdConfig.super::flavor);
    }

    @Override
    public boolean enabled() {
        return get(StatsdProperties::isEnabled, StatsdConfig.super::enabled);
    }

    @Override
    public String host() {
        return get(StatsdProperties::getHost, StatsdConfig.super::host);
    }

    @Override
    public int port() {
        return get(StatsdProperties::getPort, StatsdConfig.super::port);
    }

    @Override
    public StatsdProtocol protocol() {
        return get(StatsdProperties::getProtocol, StatsdConfig.super::protocol);
    }

    @Override
    public int maxPacketLength() {
        return get(StatsdProperties::getMaxPacketLength,
            StatsdConfig.super::maxPacketLength);
    }

    @Override
    public Duration pollingFrequency() {
        return get(StatsdProperties::getPollingFrequency,
            StatsdConfig.super::pollingFrequency);
    }

    @Override
    public boolean publishUnchangedMeters() {
        return get(StatsdProperties::isPublishUnchangedMeters, StatsdConfig.super::publishUnchangedMeters);
    }

    @Override
    public boolean buffered() {
        return get(StatsdProperties::isBuffered, StatsdConfig.super::buffered);
    }
}
