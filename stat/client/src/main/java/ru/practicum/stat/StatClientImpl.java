package ru.practicum.stat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class StatClientImpl implements StatClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatClientImpl(RestTemplateBuilder builder,
                          @Value("${stats-server.url:http://localhost:9090}") String serverUrl
    ) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.serverUrl = serverUrl;
    }

    @Override
    public void hit(EndpointHit endpointHit) {
        try {
            restTemplate.postForEntity(serverUrl + "/hit", endpointHit, Void.class);
        } catch (Exception e) {
            log.error("Не удалось отправить hit: {}", endpointHit, e);
        }
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }

        URI uri = builder.build().encode().toUri();

        try {
            ViewStats[] response = restTemplate.getForObject(uri, ViewStats[].class);
            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception e) {
            log.error(
                    "Не удалось получить статистику: start={}, end={}, uris={}, unique={}", start, end, uris, unique, e
            );
            return List.of();
        }
    }
}