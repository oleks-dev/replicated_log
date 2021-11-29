package org.example.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@UtilityClass
public class UrlUtils {
    public static URI getUri(String uri) {
        try {
            return URI.create(uri);
        } catch (Exception e) {
            log.error("Address is not correct: {}",  uri);
        }
        return null;
    }
}
