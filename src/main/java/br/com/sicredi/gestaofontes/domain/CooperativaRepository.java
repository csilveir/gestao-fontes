package br.com.sicredi.gestaofontes.domain;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@ConfigurationProperties(prefix = "cooperativas")
@Getter
public class CooperativaRepository {

    private final List<String> nomes = new ArrayList<>();
    private final Random random = new Random();

    public String getNomeCooperativa() {

        return nomes.get(random.nextInt(nomes.size()));
    }


}
