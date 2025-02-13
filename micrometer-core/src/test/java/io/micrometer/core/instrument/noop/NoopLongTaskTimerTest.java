/**
 * Copyright 2019 VMware, Inc.
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
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NoopLongTaskTimer}.
 *
 * @author Oleksii Bondar
 */
class NoopLongTaskTimerTest {

    private Id id = new Id("test", Tags.of("name", "value"), "ms", "", Type.LONG_TASK_TIMER);
    private NoopLongTaskTimer timer = new NoopLongTaskTimer(id);

    @Test
    void returnsId() {
        assertThat(timer.getId()).isEqualTo(id);
    }

    @Test
    void returnsStart() {
        assertThat(timer.start()).isNotNull();
    }

    @Test
    void returnsStop() {
        assertThat(timer.stop(1)).isEqualTo(-1);
    }

    @Test
    void returnsDuration() {
        assertThat(timer.duration(TimeUnit.MINUTES)).isEqualTo(0);
    }

    @Test
    void returnsDurationWithTask() {
        assertThat(timer.duration(0, TimeUnit.MINUTES)).isEqualTo(-1);
    }

    @Test
    void returnsActiveTasks() {
        assertThat(timer.activeTasks()).isEqualTo(0);
    }

}
