package com.shortener.url_shortener.service;

import com.shortener.url_shortener.entity.Url;
import com.shortener.url_shortener.exception.UrlNotFoundException;
import com.shortener.url_shortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final UrlRepository repo;

    public AnalyticsService(UrlRepository repo) {
        this.repo = repo;
    }

    public Url getAnalytics(String code) {
        return repo.findById(code)
                .orElseThrow(() -> new UrlNotFoundException(code));
    }
}