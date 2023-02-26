package de.schiggo.transformer.pipelinemanagement.autoconfiguration;

import de.schiggo.transformer.pipelinemanagement.properties.PipelineManagementProperties;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@ConditionalOnProperty(prefix = "pipeline-management", name = "enabled")
@ConditionalOnBean({PipelineExecutorFactory.class, PipelineGroupExecutorFactory.class})
@Configuration
@EnableJpaRepositories(
        basePackages = "de.schiggo.transformer.pipelinemanagement.persistence.repo",
        entityManagerFactoryRef = "pipelineManagementEntityManager",
        transactionManagerRef = "pipelineManagementTransactionManager"
)
public class PipelineManagementAutoConfiguration {

    @Bean
    @ConfigurationProperties("pipeline-management")
    public PipelineManagementProperties pipelineManagementProperties() {
        return new PipelineManagementProperties();
    }

    @Bean
    @ConfigurationProperties("pipeline-management.datasource")
    public DataSourceProperties pipelineManagementDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource pipelineManagementDataSource() {
        return pipelineManagementDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public SpringLiquibase pipelineManagementLiquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(pipelineManagementDataSource());
        liquibase.setChangeLog("classpath:/db/changelog/pipeline-management/changelog.xml");
        // liquibase.setContexts(properties.getContexts());
        // liquibase.setDefaultSchema(properties.getDefaultSchema());
        // liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(pipelineManagementProperties().isLiquibaseEnabled());
        // liquibase.setLabels(properties.getLabels());
        // liquibase.setChangeLogParameters(properties.getParameters());
        // liquibase.setRollbackFile(properties.getRollbackFile());*/
        return liquibase;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean pipelineManagementEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pipelineManagementDataSource());
        em.setPackagesToScan("de.schiggo.transformer.pipelinemanagement.persistence.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", pipelineManagementProperties().getHibernateDialect());
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager pipelineManagementTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(pipelineManagementEntityManager().getObject());
        return transactionManager;
    }

}