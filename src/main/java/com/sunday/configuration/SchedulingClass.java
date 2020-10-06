package com.sunday.configuration;

import com.sunday.service.SchedulerExport;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulingClass {
    private final SchedulerExport schedulerExport;

    @Scheduled(cron = "${time}")
    public void runner() {
        schedulerExport.exportCustomer();
        schedulerExport.exportStock();
    }
}
