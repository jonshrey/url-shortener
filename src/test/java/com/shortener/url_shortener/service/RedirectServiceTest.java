package com.shortener.url_shortener.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shortener.url_shortener.entity.Url;
import com.shortener.url_shortener.exception.UrlNotFoundException;
import com.shortener.url_shortener.repository.UrlRepository;

@ExtendWith(MockitoExtension.class)
class RedirectServiceTest {

    @Mock
    private UrlRepository repo;

    @InjectMocks
    private RedirectService service;

    @Test
    void shouldReturnUrlAndIncrementClicksWhenCodeExists() {
        Url url = new Url("abc123", "https://example.com", false);
        when(repo.findById("abc123")).thenReturn(Optional.of(url));
        when(repo.save(any(Url.class))).thenReturn(url);   // not strictly needed but clean

        Url result = service.getUrlAndIncrementClicks("abc123");

        assertThat(result.getClicks()).isEqualTo(1);
        assertThat(result.getOriginalUrl()).isEqualTo("https://example.com");
        verify(repo).save(url);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCodeMissing() {
        when(repo.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUrlAndIncrementClicks("unknown"))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}