package com.shortener.url_shortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "url_mappings", indexes = {
    @Index(name = "idx_long_url", columnList = "longUrl")
})
public class Url {

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public boolean getCustom() {
        return this.custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public long getClicks() {
        return this.clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Id
    @Column(length = 20)
    private String code;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false)
    private boolean custom;

    @Column(nullable = false)
    private long clicks;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Url() {
    }

    public Url(String code, String originalUrl, boolean custom) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.custom = custom;
        this.clicks = 0;
        this.createdAt = LocalDateTime.now();
    }

}