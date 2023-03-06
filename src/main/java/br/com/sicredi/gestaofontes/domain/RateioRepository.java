package br.com.sicredi.gestaofontes.domain;

import br.com.sicredi.gestaofontes.dto.RateioDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Repository
@Component
@AllArgsConstructor
public class RateioRepository {

    public static final double MULTIPLICADOR = 10000D;
    private CooperativaRepository cooperativaRepository;
    private final List<String> fontes = List.of("Poupança Rural", "LCA", "MCR 6.2", "Outras Fontes");
    private final Random random = new Random();

    public RateioDto mockRateio() {


        var rateioDto = new RateioDto();
        rateioDto.setNomeCooperativa(cooperativaRepository.getNomeCooperativa());
        rateioDto.setNomeFonte(fontes.get(random.nextInt(fontes.size())));
        rateioDto.setCredis(random.nextLong());
        rateioDto.setValorPronamp(new BigDecimal(Math.random() * MULTIPLICADOR));
        rateioDto.setValorDemais(new BigDecimal(Math.random() * MULTIPLICADOR));
        rateioDto.setValorDemais(new BigDecimal(Math.random() * MULTIPLICADOR));
        return rateioDto;
    }
}
