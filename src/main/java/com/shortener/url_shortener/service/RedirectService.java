package com.shortener.url_shortener.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shortener.url_shortener.entity.Url;
import com.shortener.url_shortener.exception.UrlNotFoundException;
import com.shortener.url_shortener.repository.UrlRepository;

@Service
public class RedirectService {

    private final UrlRepository repo;

    public RedirectService(UrlRepository repo) {
        this.repo = repo;
    }

    /**
     * Looks up the code, increments the click count, and returns the Url entity.
     * Throws UrlNotFoundException if the code does not exist.
     */
    @Transactional
    public Url getUrlAndIncrementClicks(String code) {
        Url url = repo.findById(code)
                .orElseThrow(() -> new UrlNotFoundException(code));

        url.setClicks(url.getClicks() + 1);
        repo.save(url);
        return url;
    }
}