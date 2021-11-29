package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.in.MessageDto;
import org.example.dto.out.InternalMessageDto;
import org.example.dto.Request;
import org.example.dto.Response;
import org.example.service.NotificationService;
import org.example.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RootHandler implements HttpHandler {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final Map<Integer, String> messages;
    private final NotificationService service;

    public RootHandler(String[] secondaries) {
        this.messages = new ConcurrentHashMap<>();
        this.service = new NotificationService(secondaries);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (Request.fromString(exchange.getRequestMethod())) {
            case GET:
                var msgs = new ArrayList<>(messages.entrySet());
                log.info("Return all msgs {}", msgs);
                String body = JsonUtils.listToJson(msgs);
                exchange.sendResponseHeaders(Response.OK, body.length());
                exchange.getResponseBody().write(body.getBytes());
                exchange.getRequestBody().close();
            case POST:
                var messageDto = JsonUtils.getMassage(exchange.getRequestBody(), MessageDto.class);
                if (messageDto != null) {
                    messages.putIfAbsent(count.getAndIncrement(), messageDto.getMessage());
                    log.info("Added msg: {}", messageDto);
                    service.notify(messageDto.getWriteConcern(), InternalMessageDto.of(count.get(), messageDto.getMessage()));
                    exchange.sendResponseHeaders(Response.OK, 0);
                } else {
                    log.error("Could not deserialize msg");
                    exchange.sendResponseHeaders(Response.ERROR, 0);
                }
                exchange.getRequestBody().close();
            default:
                var errorMsg = String.format("Method unsupported: %s", exchange.getRequestMethod());
                exchange.sendResponseHeaders(Response.ERROR, errorMsg.length());
                exchange.getResponseBody().write(errorMsg.getBytes());
                exchange.getRequestBody().close();
        }
    }
}
