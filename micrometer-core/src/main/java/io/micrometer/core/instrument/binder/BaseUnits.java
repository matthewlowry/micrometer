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
package io.micrometer.core.instrument.binder;

/**
 * Base units constants for convenience.
 *
 * @author Johnny Lim
 */
public final class BaseUnits {

    /**
     * For bytes.
     */
    public static final String BYTES = "bytes";

    /**
     * For rows.
     */
    public static final String ROWS = "rows";

    /**
     * For tasks.
     */
    public static final String TASKS = "tasks";

    /**
     * For threads.
     */
    public static final String THREADS = "threads";

    /**
     * For classes.
     */
    public static final String CLASSES = "classes";

    /**
     * For buffers.
     */
    public static final String BUFFERS = "buffers";

    /**
     * For events.
     */
    public static final String EVENTS = "events";

    /**
     * For files.
     */
    public static final String FILES = "files";

    /**
     * For sessions.
     */
    public static final String SESSIONS = "sessions";

    private BaseUnits() {
    }

}
