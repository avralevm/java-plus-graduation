package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.client.exception.StatisticClientException;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatClient {
    private final RestClient restClient;

    @Autowired
    public StatClient(@Value("${STATS_SERVER_URL}") String statsUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            throw new StatisticClientException("Statistics service error: " + response.getStatusText());
                        })
                .build();
    }

    public void hit(StatisticDto statisticDto) {
        try {
            log.info("Sending statistics hit request to client");
            restClient.post()
                    .uri("/hit")
                    .body(statisticDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error saving statistics in client: {}, {}", statisticDto, e.getMessage());
            throw new StatisticClientException("Error sending statistics", e);
        }
    }

    public List<GetStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            log.info("Requesting statistics from client");
            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/stats")
                            .queryParam("start", start.format(dateTimeFormat))
                            .queryParam("end", end.format(dateTimeFormat))
                            .queryParam("uris", uris != null ? uris : Collections.emptyList())
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Error retrieving statistics from client: {}", e.getMessage());
            throw new StatisticClientException("Error getting statistics", e);
        }
    }
}