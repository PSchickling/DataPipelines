package de.schiggo.transformer.pipelinemanagement.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@ConfigurationProperties(prefix="pipeline-management")
public class PipelineManagementProperties {

    private boolean enabled = false;

    private boolean liquibaseEnabled = false;

    private String hibernateDialect;


}
