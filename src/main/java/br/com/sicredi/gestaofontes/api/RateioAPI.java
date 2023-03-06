package br.com.sicredi.gestaofontes.api;

import br.com.sicredi.gestaofontes.domain.RateioRepository;
import br.com.sicredi.gestaofontes.dto.RateioDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
public class RateioAPI {


    @Value("${rateio.intervalo}")
    private int intervaloRateio;

    public RateioAPI(RateioRepository rateioRepository) {
        this.rateioRepository = rateioRepository;
    }

    private final RateioRepository rateioRepository;

    @GetMapping(path = "/rateio", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<RateioDto>> buscarRateios() {
        return Flux.interval(Duration.of(5, ChronoUnit.SECONDS))
                .map(rateio -> ServerSentEvent.<RateioDto>builder()
                        .id(UUID.randomUUID().toString())
                        .event("rateio")
                        .data(rateioRepository.mockRateio())
                        .build()
                );


    }
}
