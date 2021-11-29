package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDto;
import org.example.dto.Request;
import org.example.dto.Response;
import org.example.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class RootHandler implements HttpHandler {
    private final long delay;
    private final Map<Integer, String> messages;

    public RootHandler(long delay) {
        this.delay = delay;
        this.messages = new ConcurrentHashMap<>();
    }

    @Override
    @SneakyThrows
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
                    messages.put(messageDto.getId(), messageDto.getMessage());
                    log.info("Added msg: {}", messageDto);
                    Thread.sleep(delay);
                    exchange.sendResponseHeaders(Response.OK, -1);
                } else {
                    log.error("Could not deserialize msg");
                    exchange.sendResponseHeaders(Response.ERROR, -1);
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
