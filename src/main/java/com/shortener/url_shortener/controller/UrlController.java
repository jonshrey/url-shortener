package com.shortener.url_shortener.controller;

import com.shortener.url_shortener.dto.CreateShortUrlRequest;
import com.shortener.url_shortener.dto.CreateShortUrlResponse;
import com.shortener.url_shortener.entity.Url;
import com.shortener.url_shortener.service.RedirectService;
import com.shortener.url_shortener.service.ShortenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final ShortenService shortenService;
    private final RedirectService redirectService;

    public UrlController(ShortenService shortenService, RedirectService redirectService) {
        this.shortenService = shortenService;
        this.redirectService = redirectService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<CreateShortUrlResponse> shorten(@RequestBody CreateShortUrlRequest request) {
        String code = shortenService.shorten(request.url(), request.alias());
        String shortUrl = shortenService.buildShortUrl(code);
        return ResponseEntity.ok(new CreateShortUrlResponse(code, shortUrl));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Url url = redirectService.getUrlAndIncrementClicks(code);
        return ResponseEntity.status(301)
                .location(URI.create(url.getOriginalUrl()))
                .build();
    }
}