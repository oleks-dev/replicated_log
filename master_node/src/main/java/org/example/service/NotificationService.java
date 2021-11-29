package org.example.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.UrlUtils;
import org.example.dto.InternalMessageDto;
import org.example.utils.JsonUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NotificationService {
    private final Set<URI> secondaryUris;
    private final ExecutorService executorService;

    public NotificationService(String[] secondaryUris) {
        this.secondaryUris = Stream.of(secondaryUris)
                .map(UrlUtils::getUri)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        this.executorService = Executors.newFixedThreadPool(this.secondaryUris.size() + 1);
    }


    @SneakyThrows
    public void notify(int writeConcern, InternalMessageDto dto) {
        final CountDownLatch latch = new CountDownLatch(writeConcern);
        String body = JsonUtils.valueToJson(dto);
        secondaryUris.forEach(uri -> sendToSecondary(latch, uri, body));
        latch.await();
    }

    private void sendToSecondary(CountDownLatch latch, URI uri, String body) {
        log.info("Send message {} to secondary node: {}", body, uri.toString());
        final HttpClient httpClient = HttpClient.newBuilder()
                .executor(executorService)
                .build();
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(r -> {
                    latch.countDown();
                    log.info("Response {} from node: {}", r.statusCode(), r.uri());
                    return r;
                });
    }
}
