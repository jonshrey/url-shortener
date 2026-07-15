package com.shortener.url_shortener.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_mappings", indexes = {
    @Index(name = "idx_original_url", columnList = "original_url")
})
public class Url {

    @Id
    @Column(length = 20)
    private String code;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "is_custom", nullable = false)
    private boolean custom;

    @Column(name = "click_count", nullable = false)
    private long clicks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // No-arg constructor required by JPA
    public Url() {
    }

    public Url(String code, String originalUrl, boolean custom) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.custom = custom;
        this.clicks = 0;
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}