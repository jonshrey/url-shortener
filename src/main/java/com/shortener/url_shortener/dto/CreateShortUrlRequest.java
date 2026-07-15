package com.shortener.url_shortener.dto;

public record CreateShortUrlRequest(String url, String alias) {}