/*
 * Copyright 2023 Paul Schickling
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

package de.schiggo.transformer.pipelinemanagement.autoconfiguration;

import de.schiggo.transformer.pipelinemanagement.properties.PipelineManagementProperties;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Optional;

/**
 * Auto-Configuration file for the whole pipeline-management library.
 * <br>
 * Pipeline-Management is only used if the following property is set: <b>pipline-management.enabled=true</b>.
 */
@ConditionalOnProperty(prefix = "pipeline-management", name = "enabled", havingValue = "true")
@Configuration
public class PipelineManagementAutoConfiguration {

    @Bean
    @ConfigurationProperties("pipeline-management")
    public PipelineManagementProperties pipelineManagementProperties() {
        return new PipelineManagementProperties();
    }

    /**
     * {@link PipelineManagementServiceConfiguration} loads all service necessary for pipeline management.
     * <br>
     * The user hat to implement {@link PipelineExecutorFactory} and {@link PipelineGroupExecutorFactory}. If this is
     * not done, e.g. because the application is still in development, then this will generate simple ones to prevent
     * failures and make the application runnable.
     */
    @Configuration
    @ComponentScan(basePackages = {"de.schiggo.transformer.pipelinemanagement.service"})
    static class PipelineManagementServiceConfiguration {

        /**
         * Generate {@link PipelineGroupExecutorFactory} if such a bean does not exist.
         * <br>
         * User is expected to implement this. This one has no functionality and is only generated to make the
         * application runnable.
         */
        @Bean
        @ConditionalOnMissingBean
        public PipelineGroupExecutorFactory pipelineGroupExecutorFactory() {
            return id -> Optional.empty();
        }

        /**
         * Generate {@link PipelineExecutorFactory} if such a bean does not exist.
         * <br>
         * User is expected to implement this. This one has no functionality and is only generated to make the
         * application runnable.
         */
        @Bean
        @ConditionalOnMissingBean
        public PipelineExecutorFactory pipelineExecutorFactory() {
            return (className, pipelineAttributes, pipelineGroupAttributes, processData) -> Optional.empty();
        }

    }

    /**
     * Configures the pipeline-management with the default spring data-source.
     * <br>
     * Default spring data-source expects the beans 'entityManagerFactory' and 'transactionManager'. In case the
     * entity scan are configured manually, then maybe 'de.schiggo.transformer.pipelinemanagement.persistence.entity'
     * must be added.
     */
    @Configuration
    @ConditionalOnProperty(prefix = "pipeline-management", name = "datasource.url", havingValue = "false", matchIfMissing = true)
    @EnableJpaRepositories(
            basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.repo"
    )
    @EntityScan(basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.entity")
    static class PipelineManagementDefaultDataSourceConfiguration {

        /**
         * Run liquibase if property <b>pipeline-management.liquibase-enabled=true/b>.
         */
        @Bean(name = "pipelineManagementDefaultLiquibase")
        public SpringLiquibase pipelineManagementLiquibase(PipelineManagementProperties props, DataSource dataSource) {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog("classpath:/db/changelog/pipeline-management/changelog.xml");
            liquibase.setShouldRun(props.isLiquibaseEnabled());
            return liquibase;
        }

    }

    /**
     * Configures the pipeline-management with its own data-source, if property <b>pipeline-management.datasource.url</b>
     * is set.
     */
    @Configuration
    @ConditionalOnProperty(prefix = "pipeline-management.datasource", name = "url")
    @EnableJpaRepositories(
            basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.repo",
            entityManagerFactoryRef = "pipelineManagementEntityManager",
            transactionManagerRef = "pipelineManagementTransactionManager"
    )
    static class PipelineManagementCustomDataSourceConfiguration {

        /**
         * Load data-source properties
         */
        @Bean(name = "pipelineManagementDataSourceProperties")
        @ConfigurationProperties("pipeline-management.datasource")
        public DataSourceProperties pipelineManagementDataSourceProperties() {
            return new DataSourceProperties();
        }

        /**
         * Create data-source
         */
        @Bean(name = "pipelineManagementDataSource")
        public DataSource pipelineManagementDataSource(@Qualifier("pipelineManagementDataSourceProperties") DataSourceProperties props) {
            return props.initializeDataSourceBuilder().build();
        }

        /**
         * Run liquibase if property <b>pipeline-management.liquibase-enabled=true/b>.
         */
        @Bean(name = "pipelineManagementLiquibase")
        public SpringLiquibase pipelineManagementLiquibase(PipelineManagementProperties props, @Qualifier("pipelineManagementDataSource") DataSource dataSource) {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog("classpath:/db/changelog/pipeline-management/changelog.xml");
            liquibase.setShouldRun(props.isLiquibaseEnabled());
            return liquibase;
        }

        /**
         * Create entity manager (factory).
         */
        @Bean(name = "pipelineManagementEntityManager")
        public LocalContainerEntityManagerFactoryBean pipelineManagementEntityManager(PipelineManagementProperties props, @Qualifier("pipelineManagementDataSource") DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource);
            em.setPackagesToScan("de.schiggo.transformer.pipelinemanagement.persistence.entity");

            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            em.setJpaVendorAdapter(vendorAdapter);
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("hibernate.hbm2ddl.auto", "none");
            properties.put("hibernate.dialect", props.getHibernateDialect());
            em.setJpaPropertyMap(properties);

            return em;
        }

        /**
         * Create transaction manager.
         */
        @Bean(name = "pipelineManagementTransactionManager")
        public PlatformTransactionManager pipelineManagementTransactionManager(@Qualifier("pipelineManagementEntityManager") LocalContainerEntityManagerFactoryBean em) {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(em.getObject());
            return transactionManager;
        }
    }
}