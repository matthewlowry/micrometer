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
package io.micrometer.spring.autoconfigure.export.elastic;

import io.micrometer.elastic.ElasticConfig;
import io.micrometer.spring.autoconfigure.export.properties.StepRegistryPropertiesConfigAdapter;

/**
 * Adapter to convert {@link ElasticProperties} to an {@link ElasticConfig}.
 *
 * @author Nicolas Portmann
 */
class ElasticPropertiesConfigAdapter extends StepRegistryPropertiesConfigAdapter<ElasticProperties> implements ElasticConfig {

    ElasticPropertiesConfigAdapter(ElasticProperties properties) {
        super(properties);
    }

    @Override
    public String host() {
        return get(ElasticProperties::getHost, ElasticConfig.super::host);
    }

    @Override
    public String index() {
        return get(ElasticProperties::getIndex, ElasticConfig.super::index);
    }

    @Override
    public String indexDateFormat() {
        return get(ElasticProperties::getIndexDateFormat, ElasticConfig.super::indexDateFormat);
    }

    @Override
    public String timestampFieldName() {
        return get(ElasticProperties::getTimestampFieldName, ElasticConfig.super::timestampFieldName);
    }

    @Override
    public boolean autoCreateIndex() {
        return get(ElasticProperties::isAutoCreateIndex, ElasticConfig.super::autoCreateIndex);
    }

    @Override
    public String userName() {
        return get(ElasticProperties::getUserName, ElasticConfig.super::userName);
    }

    @Override
    public String password() {
        return get(ElasticProperties::getPassword, ElasticConfig.super::password);
    }

    @Override
    public String pipeline() {
        return get(ElasticProperties::getPipeline, ElasticConfig.super::pipeline);
    }

    @Override
    public String indexDateSeparator() {
        return get(ElasticProperties::getIndexDateSeparator, ElasticConfig.super::indexDateSeparator);
    }

}
