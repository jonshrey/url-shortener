package com.shortener.url_shortener.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;   
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shortener.url_shortener.entity.Url;
import com.shortener.url_shortener.exception.AliasAlreadyExistsException;
import com.shortener.url_shortener.repository.UrlRepository;
import com.shortener.url_shortener.util.CodeGenerator;

@ExtendWith(MockitoExtension.class)
class ShortenServiceTest {

    @Mock
    private UrlRepository repo;

    @InjectMocks
    private ShortenService service;

    @Test
    void shouldShortenValidUrlWithAutoCode() {
        String longUrl = "https://example.com";
        when(repo.findByLongUrl(longUrl)).thenReturn(Optional.empty());
        when(repo.existsById("abc12345")).thenReturn(false);
        when(repo.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0));

        // Mock the static method
        try (MockedStatic<CodeGenerator> mockedGenerator = mockStatic(CodeGenerator.class)) {
            mockedGenerator.when(() -> CodeGenerator.generate(8)).thenReturn("abc12345");

            String code = service.shorten(longUrl, null);
            assertThat(code).isEqualTo("abc12345");
        }
        verify(repo).save(any(Url.class));
    }

    @Test
    void shouldReturnExistingCodeForDuplicateUrl() {
        String longUrl = "https://example.com";
        Url existing = new Url("existing", longUrl, false);
        when(repo.findByLongUrl(longUrl)).thenReturn(Optional.of(existing));

        String code = service.shorten(longUrl, null);
        assertThat(code).isEqualTo("existing");
        verify(repo, never()).save(any(Url.class));
    }

    @Test
    void shouldAcceptCustomAlias() {
        String longUrl = "https://example.com";
        String alias = "myAlias";
        when(repo.existsById(alias)).thenReturn(false);
        when(repo.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0));

        String code = service.shorten(longUrl, alias);
        assertThat(code).isEqualTo(alias);
        verify(repo).save(argThat(url -> url.isCustom() && url.getCode().equals(alias)));
    }

    @Test
    void shouldRejectInvalidAliasFormat() {
        assertThatThrownBy(() -> service.shorten("https://example.com", "ab"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias must be");
    }

    @Test
    void shouldThrowConflictIfAliasTaken() {
        String alias = "taken";
        when(repo.existsById(alias)).thenReturn(true);

        assertThatThrownBy(() -> service.shorten("https://example.com", alias))
                .isInstanceOf(AliasAlreadyExistsException.class);
    }

    @Test
    void shouldRejectInvalidUrl() {
        assertThatThrownBy(() -> service.shorten("invalid", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid URL");
    }

    @Test
    void shouldRetryOnCodeCollision() {
        String longUrl = "https://example.com";
        when(repo.findByLongUrl(longUrl)).thenReturn(Optional.empty());
        when(repo.existsById(anyString())).thenReturn(false);
        when(repo.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<CodeGenerator> mockedGenerator = mockStatic(CodeGenerator.class)) {
            mockedGenerator.when(() -> CodeGenerator.generate(8))
                    .thenReturn("collide1", "collide2", "unique3");

            // Make collide1 and collide2 "exist" already
            when(repo.existsById("collide1")).thenReturn(true);
            when(repo.existsById("collide2")).thenReturn(true);
            when(repo.existsById("unique3")).thenReturn(false);

            String code = service.shorten(longUrl, null);
            assertThat(code).isEqualTo("unique3");
        }
    }

    @Test
    void shouldBuildShortUrl() {
        service = new ShortenService(repo, "http://localhost:8080");
        assertThat(service.buildShortUrl("abc123")).isEqualTo("http://localhost:8080/abc123");
    }
}