package com.shortener.url_shortener.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidator {

    private static final int MAX_URL_LENGTH = 2048;

    public static boolean isValid(String url) {
        if(url == null || url.isBlank()) {
            return false;
        }
        url = url.trim();
        if (url.length() > MAX_URL_LENGTH) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("http") || scheme.equals("https"))) {
                return false;
            }
            return !(uri.getHost() == null || uri.getHost().isBlank());
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValidAlias(String alias) {
        return alias != null && alias.matches("^[a-zA-Z0-9]{4,20}$");
    }
}