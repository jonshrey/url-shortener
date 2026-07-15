package com.shortener.url_shortener.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shortener.url_shortener.entity.Url;
import com.shortener.url_shortener.exception.AliasAlreadyExistsException;   // static import
import com.shortener.url_shortener.repository.UrlRepository;
import com.shortener.url_shortener.util.CodeGenerator;
import com.shortener.url_shortener.util.UrlValidator;

@Service
public class ShortenService {

    private final UrlRepository repo;
    private final String baseUrl;

    public ShortenService(UrlRepository repo,
                          @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.repo = repo;
        this.baseUrl = baseUrl;
    }

    public String shorten(String longUrl, String customAlias) {
        if (!UrlValidator.isValid(longUrl)) {
            throw new IllegalArgumentException("Invalid URL");
        }

        if (customAlias != null && !customAlias.isBlank()) {
            if (!UrlValidator.isValidAlias(customAlias)) {
                throw new IllegalArgumentException("Alias must be 4-20 alphanumeric characters");
            }
            if (repo.existsById(customAlias)) {
                throw new AliasAlreadyExistsException(customAlias);
            }
            Url mapping = new Url(customAlias, longUrl, true);
            repo.save(mapping);
            return customAlias;
        }

        var existing = repo.findByOriginalUrl(longUrl);
        if (existing.isPresent()) {
            return existing.get().getCode();
        }

        String code;
        int attempts = 0;
        do {
            if (attempts > 10) {
                throw new RuntimeException("Failed to generate unique code after 10 attempts");
            }
            code = CodeGenerator.generate(8);   
            attempts++;
        } while (repo.existsById(code));

        Url newUrl = new Url(code, longUrl, false);
        repo.save(newUrl);
        return code;
    }

    public String buildShortUrl(String code) {
        return baseUrl + "/" + code;
    }
}