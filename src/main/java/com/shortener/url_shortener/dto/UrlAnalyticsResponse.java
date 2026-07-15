package com.shortener.url_shortener.dto;

import java.time.LocalDateTime;

public record UrlAnalyticsResponse(
    String code,
    String longUrl,
    long clicks,
    LocalDateTime createdAt,
    boolean custom
) {}