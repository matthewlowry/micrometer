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
package io.micrometer.spring.autoconfigure.export.ganglia;

import info.ganglia.gmetric4j.gmetric.GMetric;
import io.micrometer.ganglia.GangliaConfig;
import io.micrometer.spring.autoconfigure.export.properties.PropertiesConfigAdapter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Adapter to convert {@link GangliaProperties} to a {@link GangliaConfig}.
 *
 * @author Jon Schneider
 * @author Phillip Webb
 */
class GangliaPropertiesConfigAdapter extends PropertiesConfigAdapter<GangliaProperties>
    implements GangliaConfig {

    GangliaPropertiesConfigAdapter(GangliaProperties properties) {
        super(properties);
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public boolean enabled() {
        return get(GangliaProperties::isEnabled, GangliaConfig.super::enabled);
    }

    @Override
    public Duration step() {
        return get(GangliaProperties::getStep, GangliaConfig.super::step);
    }

    @Override
    public TimeUnit rateUnits() {
        return get(GangliaProperties::getRateUnits, GangliaConfig.super::rateUnits);
    }

    @Override
    public TimeUnit durationUnits() {
        return get(GangliaProperties::getDurationUnits,
            GangliaConfig.super::durationUnits);
    }

    @Override
    public String protocolVersion() {
        return get(GangliaProperties::getProtocolVersion,
            GangliaConfig.super::protocolVersion);
    }

    @Override
    public GMetric.UDPAddressingMode addressingMode() {
        return get(GangliaProperties::getAddressingMode,
            GangliaConfig.super::addressingMode);
    }

    @Override
    public int ttl() {
        return get(GangliaProperties::getTimeToLive, GangliaConfig.super::ttl);
    }

    @Override
    public String host() {
        return get(GangliaProperties::getHost, GangliaConfig.super::host);
    }

    @Override
    public int port() {
        return get(GangliaProperties::getPort, GangliaConfig.super::port);
    }

}
