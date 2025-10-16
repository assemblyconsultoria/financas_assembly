package com.financas.assembly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Financial Assembly System.
 *
 * This application provides a comprehensive financial management solution
 * for tracking cash flow for individual clients (pessoas físicas) and
 * corporate clients (empresas).
 *
 * @author Financial Assembly Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class FinancialAssemblyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialAssemblyApplication.class, args);
    }

}
