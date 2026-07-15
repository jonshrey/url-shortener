package com.shortener.url_shortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shortener.url_shortener.entity.Url;

public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByOriginalUrl(String originalUrl);
}