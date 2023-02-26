package de.schiggo.transformer.demo.app.config;

import de.schiggo.transformer.pipelinemanagement.service.PipelineManagerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final PipelineManagerServiceImpl pipelineManagerService;

    public void run(ApplicationArguments args) {
        pipelineManagerService.addPipelineGroup("group1", "", "", new HashMap<>());
        pipelineManagerService.addPipeline("pipeline1", "", "pipeline1", new HashMap<>(), "group1");
        pipelineManagerService.addPipeline("pipeline2", "", "pipeline2", new HashMap<>(), "group1");
        pipelineManagerService.requiresFinish("pipeline1", "pipeline2");
    }
}