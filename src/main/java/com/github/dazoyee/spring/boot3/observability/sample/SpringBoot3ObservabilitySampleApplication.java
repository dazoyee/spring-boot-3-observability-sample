package com.github.dazoyee.spring.boot3.observability.sample;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.observation.aop.ObservedAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringBoot3ObservabilitySampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3ObservabilitySampleApplication.class, args);
    }

    @Configuration(proxyBeanMethods = false)
    public static class MyConfiguration {

        // @Observed をサポートするには、このアスペクトを登録する必要があります
        @Bean
        ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
            return new ObservedAspect(observationRegistry);
        }
    }

    @RestController
    public static class SampleController {

        private final SampleService service;

        public SampleController(SampleService service) {
            this.service = service;
        }

        @GetMapping(value = "/")
        public String getMapping() {
            return service.service();
        }
    }

    @Service
    public static class SampleService {

        private static final Logger log = LoggerFactory.getLogger(SampleService.class);

        // アノテーションを使用してメソッドを観察する例
        // <metric.name> はメトリック名として使用されます
        // <observed-service> はスパン名として使用されます
        // <key=value> はメトリックとスパンの両方のタグとして設定されます
        @Observed(
                name = "metric.name",
                contextualName = "observed-service",
                lowCardinalityKeyValues = {"key", "value"})
        public String service() {
            log.info("hello");
            return "hello";
        }
    }
}
