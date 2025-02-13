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
package io.micrometer.statsd.internal;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.lang.Nullable;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SysdigStatsdLineBuilder extends FlavorStatsdLineBuilder {
    private final Object tagsLock = new Object();
    @SuppressWarnings({"NullableProblems", "unused"})
    private volatile NamingConvention namingConvention;
    @SuppressWarnings("NullableProblems")
    private volatile String name;
    @Nullable
    private volatile String conventionTags;
    @SuppressWarnings("NullableProblems")
    private volatile String tagsNoStat;
    private volatile PMap<Statistic, String> tags = HashTreePMap.empty();

    private static final Pattern NAME_WHITELIST = Pattern.compile("[^\\w._]");

    public SysdigStatsdLineBuilder(Meter.Id id, MeterRegistry.Config config) {
        super(id, config);
    }

    @Override
    String line(String amount, @Nullable Statistic stat, String type) {
        updateIfNamingConventionChanged();
        return name + tagsByStatistic(stat) + ":" + amount + "|" + type;
    }

    private void updateIfNamingConventionChanged() {
        NamingConvention next = config.namingConvention();
        if (this.namingConvention != next) {
            this.name = sanitize(next.name(id.getName(), id.getType(), id.getBaseUnit()));
            this.tags = HashTreePMap.empty();
            this.conventionTags = id.getTagsAsIterable().iterator().hasNext() ?
                    id.getConventionTags(next).stream()
                            .map(t -> sanitize(t.getKey()) + "=" + sanitize(t.getValue()))
                            .collect(Collectors.joining(","))
                    : null;
            this.tagsNoStat = tags(null, conventionTags, "=", "#");
            this.namingConvention = next;
        }
    }

    private static String sanitize(String name) {
        return NAME_WHITELIST.matcher(name).replaceAll("_");
    }

    private String tagsByStatistic(@Nullable Statistic stat) {
        if (stat == null) {
            return tagsNoStat;
        }

        String tagString = tags.get(stat);
        if (tagString != null)
            return tagString;

        synchronized (tagsLock) {
            tagString = tags.get(stat);
            if (tagString != null) {
                return tagString;
            }

            tagString = tags(stat, conventionTags, "=", "#");
            tags = tags.plus(stat, tagString);
            return tagString;
        }
    }
}
