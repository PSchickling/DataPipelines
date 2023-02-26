package de.schiggo.transformer.pipelinemanagement.autoconfiguration;

import de.schiggo.transformer.pipelinemanagement.properties.PipelineManagementProperties;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

@ConditionalOnProperty(prefix = "pipeline-management", name = "enabled", havingValue = "true")
@ConditionalOnBean({PipelineExecutorFactory.class, PipelineGroupExecutorFactory.class})
@Configuration
public class PipelineManagementAutoConfiguration {

    @Bean
    @ConfigurationProperties("pipeline-management")
    public PipelineManagementProperties pipelineManagementProperties() {
        return new PipelineManagementProperties();
    }

    @Configuration
    @ComponentScan(basePackages = {"de.schiggo.transformer.pipelinemanagement.service"})
    static class PipelineManagementServiceConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public PipelineGroupExecutorFactory pipelineGroupExecutorFactory() {
            return id -> Optional.empty();
        }

        @Bean
        @ConditionalOnMissingBean
        public PipelineExecutorFactory pipelineExecutorFactory() {
            return (className, pipelineAttributes, pipelineGroupAttributes, processData) -> Optional.empty();
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "pipeline-management", name = "datasource.url", havingValue = "false", matchIfMissing = true)
    @EnableJpaRepositories(
            basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.repo"
    )
    @EntityScan(basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.entity")
    static class PipelineManagementDefaultDataSourceConfiguration {

        @Bean(name = "pipelineManagementDefaultLiquibase")
        public SpringLiquibase pipelineManagementLiquibase(PipelineManagementProperties props, DataSource dataSource) {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog("classpath:/db/changelog/pipeline-management/changelog.xml");
            liquibase.setShouldRun(props.isLiquibaseEnabled());
            return liquibase;
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "pipeline-management.datasource", name = "url")
    @EnableJpaRepositories(
            basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.repo",
            entityManagerFactoryRef = "pipelineManagementEntityManager",
            transactionManagerRef = "pipelineManagementTransactionManager"
    )
    static class PipelineManagementCustomDataSourceConfiguration {

        @Bean(name = "pipelineManagementDataSourceProperties")
        @ConfigurationProperties("pipeline-management.datasource")
        public DataSourceProperties pipelineManagementDataSourceProperties() {
            return new DataSourceProperties();
        }

        @Bean(name = "pipelineManagementDataSource")
        public DataSource pipelineManagementDataSource(@Qualifier("pipelineManagementDataSourceProperties") DataSourceProperties props) {
            return props.initializeDataSourceBuilder().build();
        }

        @Bean(name = "pipelineManagementLiquibase")
        public SpringLiquibase pipelineManagementLiquibase(PipelineManagementProperties props, @Qualifier("pipelineManagementDataSource") DataSource dataSource) {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog("classpath:/db/changelog/pipeline-management/changelog.xml");
            liquibase.setShouldRun(props.isLiquibaseEnabled());
            return liquibase;
        }

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

        @Bean(name = "pipelineManagementTransactionManager")
        public PlatformTransactionManager pipelineManagementTransactionManager(@Qualifier("pipelineManagementEntityManager") LocalContainerEntityManagerFactoryBean em) {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(em.getObject());
            return transactionManager;
        }
    }
}