package com.example.tengyunapibackend.config;

import com.example.tengyunapibackend.model.vo.TextRepairReport;
import com.example.tengyunapibackend.service.TextDataRepairService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(20)
public class TextDataRepairRunner implements CommandLineRunner {

    @Resource
    private TextDataRepairService textDataRepairService;

    @Value("${app.text-repair.run-on-startup:true}")
    private boolean runOnStartup;

    @Value("${app.text-repair.dry-run-on-startup:false}")
    private boolean dryRunOnStartup;

    @Override
    public void run(String... args) {
        if (!runOnStartup) {
            return;
        }
        TextRepairReport report = textDataRepairService.repairMojibake(dryRunOnStartup);
        log.info("Text repair startup run finished. dryRun={}, totalUpdated={}, userNameUpdated={}, interfaceNameUpdated={}, interfaceDescUpdated={}, userPlaceholder={}, interfaceNamePlaceholder={}, interfaceDescPlaceholder={}",
                report.isDryRun(),
                report.getTotalUpdatedCount(),
                report.getUpdatedUserNameCount(),
                report.getUpdatedInterfaceNameCount(),
                report.getUpdatedInterfaceDescriptionCount(),
                report.getPlaceholderUserNameCount(),
                report.getPlaceholderInterfaceNameCount(),
                report.getPlaceholderInterfaceDescriptionCount());
        if (!report.getSamples().isEmpty()) {
            log.info("Text repair samples: {}", report.getSamples());
        }
    }
}

