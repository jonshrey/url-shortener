package com.shortener.url_shortener.dto;

import java.time.LocalDateTime;

public record UrlAnalyticsResponse(
    String code,
    String originalUrl,
    long clicks,
    LocalDateTime createdAt,
    boolean custom
) {}