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
package io.micrometer.core.instrument.push;

import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.ipc.http.HttpSender;

import java.time.Duration;

/**
 * Common configuration settings for any registry that pushes aggregated
 * metrics on a regular interval.
 *
 * @author Jon Schneider
 */
public interface PushRegistryConfig extends MeterRegistryConfig {
    /**
     * @return The step size (reporting frequency) to use. The default is 1 minute.
     */
    default Duration step() {
        String v = get(prefix() + ".step");
        return v == null ? Duration.ofMinutes(1) : Duration.parse(v);
    }

    /**
     * @return {@code true} if publishing is enabled. Default is {@code true}.
     */
    default boolean enabled() {
        String v = get(prefix() + ".enabled");
        return v == null || Boolean.valueOf(v);
    }

    /**
     * Return the number of threads to use with the scheduler.
     *
     * Note that this configuration is NOT supported.
     *
     * @return The number of threads to use with the scheduler. The default is
     * 2 threads.
     * @deprecated since 1.1.13 because this configuration is not used
     */
    @Deprecated
    default int numThreads() {
        String v = get(prefix() + ".numThreads");
        return v == null ? 2 : Integer.parseInt(v);
    }

    /**
     * @return The connection timeout for requests to the backend. The default is
     * 1 second.
     * @deprecated Connect timeout and read timeout have different meanings depending on the HTTP client. Configure
     * timeout options on your {@link HttpSender} of choice instead.
     */
    @Deprecated
    default Duration connectTimeout() {
        String v = get(prefix() + ".connectTimeout");
        return v == null ? Duration.ofSeconds(1) : Duration.parse(v);
    }

    /**
     * @return The read timeout for requests to the backend. The default is
     * 10 seconds.
     * @deprecated Connect timeout and read timeout have different meanings depending on the HTTP client. Configure
     * timeout options on your {@link HttpSender} of choice instead.
     */
    @Deprecated
    default Duration readTimeout() {
        String v = get(prefix() + ".readTimeout");
        return v == null ? Duration.ofSeconds(10) : Duration.parse(v);
    }

    /**
     * @return The number of measurements per request to use for the backend. If more
     * measurements are found, then multiple requests will be made. The default is
     * 10,000.
     */
    default int batchSize() {
        String v = get(prefix() + ".batchSize");
        return v == null ? 10000 : Integer.parseInt(v);
    }
}
