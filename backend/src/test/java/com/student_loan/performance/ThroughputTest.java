package com.student_loan.performance;

import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;

import com.student_loan.service.LoanService;
import com.student_loan.service.ItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("performance")
@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
public class ThroughputTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ItemService itemService;

    @JUnitPerfTestActiveConfig
    private static final com.github.noconnor.junitperf.JUnitPerfReportingConfig PERF_CONFIG =
        com.github.noconnor.junitperf.JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator(
                System.getProperty("user.dir") + "/target/reports/loan-service-throughput.html"))
            .build();

    @BeforeEach
    void setup() {
        // Any setup code if needed
    }

    @Test
    @JUnitPerfTest(threads = 30, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(executionsPerSec = 100)
    public void testGetAllItemsThroughput() {
        itemService.getAllItems();
    }
}
