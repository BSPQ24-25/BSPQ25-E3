package com.student_loan.performance;

import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;

import com.student_loan.service.LoanService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("performance")
@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
public class DurationTest {

    @Autowired
    private LoanService loanService;

    @JUnitPerfTestActiveConfig
    private static final com.github.noconnor.junitperf.JUnitPerfReportingConfig PERF_CONFIG =
        com.github.noconnor.junitperf.JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator(
                System.getProperty("user.dir") + "/target/reports/loan-service-duration-perf-report.html"))
            .build();

    @BeforeEach
    void setup() {
        // Pre-test setup if needed
    }

    @Test
    @JUnitPerfTest(threads = 20, durationMs = 15000, warmUpMs = 2000)
    @JUnitPerfTestRequirement(percentiles = "95:1000ms")
    public void testGetAllLoansStrictDuration() {
        loanService.getAllLoans();
    }
}