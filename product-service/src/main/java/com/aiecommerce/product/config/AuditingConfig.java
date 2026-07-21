package com.aiecommerce.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditingConfig {
    @Bean
    public AuditorAware<String> auditorProvider(){
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                // tich hop spring security
                return Optional.of("zoloft");
            }
        };
    }
}
