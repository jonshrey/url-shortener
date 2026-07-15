package com.shortener.url_shortener.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class UrlValidatorTest {

    @Test
    void shouldAcceptValidHttpUrl() {
        assertThat(UrlValidator.isValid("http://example.com")).isTrue();
    }

    @Test
    void shouldAcceptValidHttpsUrl() {
        assertThat(UrlValidator.isValid("https://example.com/path?q=1")).isTrue();
    }

    @Test
    void shouldRejectMissingScheme() {
        assertThat(UrlValidator.isValid("example.com")).isFalse();
    }

    @Test
    void shouldRejectInvalidScheme() {
        assertThat(UrlValidator.isValid("ftp://example.com")).isFalse();
    }

    @Test
    void shouldRejectEmptyHost() {
        assertThat(UrlValidator.isValid("https:///path")).isFalse();
    }

    @Test
    void shouldRejectNullUrl() {
        assertThat(UrlValidator.isValid(null)).isFalse();
    }

    @Test
    void shouldRejectVeryLongUrl() {
        String longUrl = "https://" + "a".repeat(3000);
        assertThat(UrlValidator.isValid(longUrl)).isFalse();
    }

    @Test
    void shouldAcceptValidAlias() {
        assertThat(UrlValidator.isValidAlias("myLink1")).isTrue();
    }

    @Test
    void shouldRejectAliasTooShort() {
        assertThat(UrlValidator.isValidAlias("ab")).isFalse();
    }

    @Test
    void shouldRejectAliasTooLong() {
        assertThat(UrlValidator.isValidAlias("a".repeat(21))).isFalse();
    }

    @Test
    void shouldRejectAliasWithSpecialChars() {
        assertThat(UrlValidator.isValidAlias("my-link")).isFalse();
    }

    @Test
    void shouldRejectNullAlias() {
        assertThat(UrlValidator.isValidAlias(null)).isFalse();
    }
}