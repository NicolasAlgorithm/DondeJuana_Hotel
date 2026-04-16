package com.project.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafRootTemplateConfig {

    @Bean
    public ITemplateResolver rootFileTemplateResolver(
            @Value("${app.templates.external-prefix:file:./}") String externalPrefix,
            @Value("${app.templates.cache:false}") boolean cacheable
    ) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setOrder(0);
        resolver.setCheckExistence(true);
        resolver.setPrefix(normalizePrefix(externalPrefix));
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(cacheable);
        return resolver;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "file:./";
        }

        String normalized = prefix.trim();
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }
}
