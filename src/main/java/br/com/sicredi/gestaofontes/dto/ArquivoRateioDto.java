package br.com.sicredi.gestaofontes.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(of = "codigoCredis")
public class ArquivoRateioDto {

    private BigDecimal codigoCredis;
    private String nomeCooperativa;

    private RateioDemaisDto rateioDemais;

    private PronampDto pronamp;

    private PronafDto pronaf;
}
