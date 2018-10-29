/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.boot.admin.server.config;

import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.entities.SnapshottingInstanceRepository;
import de.codecentric.boot.admin.server.eventstore.ConcurrentMapEventStore;
import de.codecentric.boot.admin.server.eventstore.HazelcastEventStore;
import de.codecentric.boot.admin.server.eventstore.InstanceEventStore;
import de.codecentric.boot.admin.server.notify.MailNotifier;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hazelcast.config.Config;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminServerAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void simpleConfig() {
        load();

        assertThat(context.getBean(InstanceRepository.class)).isInstanceOf(SnapshottingInstanceRepository.class);
        assertThat(context.getBeansOfType(MailNotifier.class)).isEmpty();
        assertThat(context.getBean(InstanceEventStore.class)).isInstanceOf(ConcurrentMapEventStore.class);
    }

    @Test
    public void hazelcastConfig() {
        load(TestHazelcastConfig.class);
        assertThat(context.getBean(InstanceEventStore.class)).isInstanceOf(HazelcastEventStore.class);
    }

    @Configuration
    static class TestHazelcastConfig {
        @Bean
        public Config config() {
            return new Config();
        }
    }

    private void load(String... environment) {
        load(null, environment);
    }

    private void load(Class<?> config, String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        if (config != null) {
            applicationContext.register(config);
        }
        applicationContext.register(RestTemplateAutoConfiguration.class);
        applicationContext.register(HazelcastAutoConfiguration.class);
        applicationContext.register(AdminServerMarkerConfiguration.class);
        applicationContext.register(AdminServerHazelcastAutoConfiguration.class);
        applicationContext.register(AdminServerAutoConfiguration.class);

        TestPropertyValues.of(environment).applyTo(applicationContext);
        applicationContext.refresh();
        this.context = applicationContext;
    }
}
