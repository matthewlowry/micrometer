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
package io.micrometer.spring.web.client;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.lang.Nullable;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Default implementation of {@link RestTemplateExchangeTagsProvider}.
 *
 * @author Jon Schneider
 */
public class DefaultRestTemplateExchangeTagsProvider
    implements RestTemplateExchangeTagsProvider {

    @Override
    public Iterable<Tag> getTags(@Nullable String urlTemplate, HttpRequest request,
                                 @Nullable ClientHttpResponse response) {
        Tag uriTag = urlTemplate != null && StringUtils.hasText(urlTemplate)
            ? RestTemplateExchangeTags.uri(urlTemplate)
            : RestTemplateExchangeTags.uri(request);
        return Arrays.asList(RestTemplateExchangeTags.method(request), uriTag,
            RestTemplateExchangeTags.status(response),
            RestTemplateExchangeTags.clientName(request),
            RestTemplateExchangeTags.outcome(response));
    }
}
