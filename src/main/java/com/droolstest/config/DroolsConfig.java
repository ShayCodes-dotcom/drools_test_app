package com.droolstest.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * Drools engine configuration.
 * Scans classpath:rules/*.drl for rule files and compiles them
 * into a KieContainer that produces KieSession instances.
 */
@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load all .drl files from classpath:rules/
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] ruleFiles = resolver.getResources("classpath:rules/*.drl");

        for (Resource ruleFile : ruleFiles) {
            String path = "src/main/resources/rules/" + ruleFile.getFilename();
            kieFileSystem.write(path,
                    kieServices.getResources().newInputStreamResource(ruleFile.getInputStream()));
            System.out.println("Loaded DRL: " + ruleFile.getFilename());
        }

        // Build and verify
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        Results results = kieBuilder.getResults();

        if (results.hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException(
                    "DRL compilation errors:\n" + results.getMessages());
        }

        if (results.hasMessages(Message.Level.WARNING)) {
            System.out.println("DRL compilation warnings:\n" + results.getMessages());
        }

        return kieServices.newKieContainer(
                kieServices.getRepository().getDefaultReleaseId());
    }
}
