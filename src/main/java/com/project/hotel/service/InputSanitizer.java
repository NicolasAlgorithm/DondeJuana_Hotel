package com.project.hotel.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class InputSanitizer {

    public String sanitizePlainText(String raw) {
        if (raw == null) {
            return null;
        }

        String sanitized = Jsoup.clean(raw, Safelist.none());
        sanitized = sanitized.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", " ");
        sanitized = sanitized.replaceAll("\\s+", " ").trim();
        return sanitized;
    }

    public String sanitizeUpperCode(String raw) {
        String sanitized = sanitizePlainText(raw);
        return sanitized == null ? null : sanitized.toUpperCase(Locale.ROOT);
    }
}
