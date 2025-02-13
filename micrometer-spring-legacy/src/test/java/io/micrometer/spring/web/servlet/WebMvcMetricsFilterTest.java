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
package io.micrometer.spring.web.servlet;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.lang.NonNull;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.spring.autoconfigure.web.servlet.WebMvcMetricsAutoConfiguration;
import io.prometheus.client.CollectorRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;

import static io.micrometer.spring.web.servlet.WebMvcMetricsFilterTest.RedirectAndNotFoundFilter.TEST_MISBEHAVE_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jon Schneider
 * @author Nikolay Rybak
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@TestPropertySource(properties = "security.ignored=/**")
public class WebMvcMetricsFilterTest {
    @Autowired
    private SimpleMeterRegistry registry;

    @Autowired
    private PrometheusMeterRegistry prometheusRegistry;

    @Autowired
    private MockMvc mvc;

    @Autowired
    @Qualifier("callableBarrier")
    private CyclicBarrier callableBarrier;

    @Autowired
    @Qualifier("completableFutureBarrier")
    private CyclicBarrier completableFutureBarrier;

    @Test
    public void timedMethod() throws Exception {
        mvc.perform(get("/api/c1/10")).andExpect(status().isOk());

        assertThat(this.registry.get("http.server.requests")
                .tags("status", "200", "uri", "/api/c1/{id}", "public", "true")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void subclassedTimedMethod() throws Exception {
        mvc.perform(get("/api/c1/metaTimed/10")).andExpect(status().isOk());

        assertThat(this.registry.get("http.server.requests")
                .tags("status", "200", "uri", "/api/c1/metaTimed/{id}")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void untimedMethod() throws Exception {
        mvc.perform(get("/api/c1/untimed/10")).andExpect(status().isOk());

        assertThat(this.registry.find("http.server.requests")
                .tags("uri", "/api/c1/untimed/10").timer())
                .isNull();
    }

    @Test
    public void timedControllerClass() throws Exception {
        mvc.perform(get("/api/c2/10")).andExpect(status().isOk());

        assertThat(this.registry.get("http.server.requests").tags("status", "200")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void badClientRequest() throws Exception {
        mvc.perform(get("/api/c1/oops")).andExpect(status().is4xxClientError());

        assertThat(this.registry.get("http.server.requests").tags("status", "400")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void redirectRequest() throws Exception {
        mvc.perform(get("/api/redirect")
                .header(TEST_MISBEHAVE_HEADER, "302")).andExpect(status().is3xxRedirection());

        this.registry.get("http.server.requests")
                .tags("uri", "REDIRECTION", "status", "302").timer();
    }

    @Test
    public void notFoundRequest() throws Exception {
        mvc.perform(get("/api/not/found")
                .header(TEST_MISBEHAVE_HEADER, "404"))
                .andExpect(status().is4xxClientError());

        this.registry.get("http.server.requests")
                .tags("uri", "NOT_FOUND", "status", "404").timer();
    }

    @Test
    public void unhandledError() {
        assertThatCode(() -> mvc.perform(get("/api/c1/unhandledError/10"))
                .andExpect(status().isOk()))
                .hasRootCauseInstanceOf(RuntimeException.class);

        assertThat(this.registry.get("http.server.requests")
                .tags("exception", "RuntimeException")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void streamingError() throws Exception {
        MvcResult result = this.mvc.perform(get("/api/c1/streamingError"))
                .andExpect(request().asyncStarted()).andReturn();
        assertThatCode(
                () -> this.mvc.perform(asyncDispatch(result)).andExpect(status().isOk()));
        assertThat(this.registry.get("http.server.requests")
                .tags("exception", "IOException").timer().count()).isEqualTo(1L);
    }

    @Test
    public void endpointThrowsError() throws Exception {
        mvc.perform(get("/api/c1/error/10")).andExpect(status().is4xxClientError());

        assertThat(this.registry.get("http.server.requests").tags("status", "422")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void endpointThrowsAnonymousError() throws Exception {
        try {
            mvc.perform(get("/api/c1/anonymousError/10"));
        } catch (Throwable ignore) {
        }

        assertThat(this.registry.get("http.server.requests").tag("uri", "/api/c1/anonymousError/{id}").timer().getId()
                .getTag("exception"))
                .endsWith("$1");
    }

    @Test
    public void regexBasedRequestMapping() throws Exception {
        mvc.perform(get("/api/c1/regex/.abc")).andExpect(status().isOk());

        assertThat(this.registry.get("http.server.requests")
                .tags("uri", "/api/c1/regex/{id:\\.[a-z]+}")
                .timer().count()).isEqualTo(1L);
    }

    @Test
    public void recordQuantiles() throws Exception {
        mvc.perform(get("/api/c1/percentiles/10")).andExpect(status().isOk());

        assertThat(prometheusRegistry.scrape()).contains("quantile=\"0.5\"");
        assertThat(prometheusRegistry.scrape()).contains("quantile=\"0.95\"");
    }

    @Test
    public void recordHistogram() throws Exception {
        mvc.perform(get("/api/c1/histogram/10")).andExpect(status().isOk());

        assertThat(prometheusRegistry.scrape()).contains("le=\"0.001\"");
        assertThat(prometheusRegistry.scrape()).contains("le=\"30.0\"");
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Timed(percentiles = 0.95)
    public @interface Timed95 {
    }

    @Configuration
    @EnableWebMvc
    @Import({Controller1.class, Controller2.class})
    @ImportAutoConfiguration(WebMvcMetricsAutoConfiguration.class)
    static class MetricsFilterApp {
        @Bean
        Clock micrometerClock() {
            return new MockClock();
        }

        @Primary
        @Bean
        MeterRegistry meterRegistry(Collection<MeterRegistry> registries, Clock clock) {
            CompositeMeterRegistry composite = new CompositeMeterRegistry(clock);
            registries.forEach(composite::add);
            return composite;
        }

        @Bean
        SimpleMeterRegistry simple(Clock clock) {
            return new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);
        }

        @Bean
        PrometheusMeterRegistry prometheus(Clock clock) {
            PrometheusMeterRegistry r = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, new CollectorRegistry(), clock);
            r.config().meterFilter(new MeterFilter() {
                @Override
                @NonNull
                public MeterFilterReply accept(@NonNull Meter.Id id) {
                    for (Tag tag : id.getTags()) {
                        if (tag.getKey().equals("uri") && (tag.getValue().contains("histogram") || tag.getValue().contains("percentiles"))) {
                            return MeterFilterReply.ACCEPT;
                        }
                    }
                    return MeterFilterReply.DENY;
                }
            });
            return r;
        }

        @Bean
        RedirectAndNotFoundFilter redirectAndNotFoundFilter() {
            return new RedirectAndNotFoundFilter();
        }

        @Bean(name = "callableBarrier")
        CyclicBarrier callableBarrier() {
            return new CyclicBarrier(2);
        }

        @Bean(name = "completableFutureBarrier")
        CyclicBarrier completableFutureBarrier() {
            return new CyclicBarrier(2);
        }
    }

    @RestController
    @RequestMapping("/api/c1")
    static class Controller1 {
        @Autowired
        @Qualifier("callableBarrier")
        private CyclicBarrier callableBarrier;

        @Autowired
        @Qualifier("completableFutureBarrier")
        private CyclicBarrier completableFutureBarrier;

        @Timed(extraTags = {"public", "true"})
        @GetMapping("/{id}")
        public String successfulWithExtraTags(@PathVariable Long id) {
            return id.toString();
        }

        @Timed
        @Timed(value = "my.long.request", extraTags = {"region", "test"}, longTask = true)
        @GetMapping("/callable/{id}")
        public Callable<String> asyncCallable(@PathVariable Long id) throws Exception {
            callableBarrier.await();
            return () -> {
                try {
                    callableBarrier.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return id.toString();
            };
        }

        @Timed
        @GetMapping("/completableFuture/{id}")
        CompletableFuture<String> asyncCompletableFuture(@PathVariable Long id) throws Exception {
            completableFutureBarrier.await();
            return CompletableFuture.supplyAsync(() -> {
                try {
                    completableFutureBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                return id.toString();
            });
        }

        @Timed
        @Timed(value = "my.long.request.exception", longTask = true)
        @GetMapping("/completableFutureException")
        CompletableFuture<String> asyncCompletableFutureException() {
            return CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("boom");
            });
        }

        @GetMapping("/untimed/{id}")
        public String successfulButUntimed(@PathVariable Long id) {
            return id.toString();
        }

        @Timed
        @GetMapping("/error/{id}")
        public String alwaysThrowsException(@PathVariable Long id) {
            throw new IllegalStateException("Boom on " + id + "!");
        }

        @Timed
        @GetMapping("/anonymousError/{id}")
        public String alwaysThrowsAnonymousException(@PathVariable Long id) throws Exception {
            throw new Exception("this exception won't have a simple class name") {
            };
        }

        @Timed
        @GetMapping("/unhandledError/{id}")
        public String alwaysThrowsUnhandledException(@PathVariable Long id) {
            throw new RuntimeException("Boom on " + id + "!");
        }

        @GetMapping("/streamingError")
        public ResponseBodyEmitter streamingError() {
            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
            emitter.completeWithError(
                    new IOException("error while writing to the response"));
            return emitter;
        }

        @Timed
        @GetMapping("/regex/{id:\\.[a-z]+}")
        public String successfulRegex(@PathVariable String id) {
            return id;
        }

        @Timed(percentiles = {0.50, 0.95})
        @GetMapping("/percentiles/{id}")
        public String percentiles(@PathVariable String id) {
            return id;
        }

        @Timed(histogram = true)
        @GetMapping("/histogram/{id}")
        public String histogram(@PathVariable String id) {
            return id;
        }

        @Timed95
        @GetMapping("/metaTimed/{id}")
        public String meta(@PathVariable String id) {
            return id;
        }

        @ExceptionHandler(IllegalStateException.class)
        @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
        ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) {
            return new ModelAndView("myerror");
        }
    }

    @RestController
    @Timed
    @RequestMapping("/api/c2")
    static class Controller2 {
        @GetMapping("/{id}")
        public String successful(@PathVariable Long id) {
            return id.toString();
        }
    }

    @NonNullApi
    static class RedirectAndNotFoundFilter extends OncePerRequestFilter {

        static final String TEST_MISBEHAVE_HEADER = "x-test-misbehave-status";

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String misbehave = request.getHeader(TEST_MISBEHAVE_HEADER);
            if (misbehave != null) {
                response.setStatus(Integer.parseInt(misbehave));
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
