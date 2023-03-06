package br.com.sicredi.gestaofontes.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RateioDto {

    private Long credis;
    private String nomeCooperativa;
    private String nomeFonte;

    private BigDecimal valorPronaf;

    private BigDecimal valorPronamp;

    private BigDecimal valorDemais;

}
