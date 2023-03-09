package br.com.sicredi.gestaofontes.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PronampDto {

    private BigDecimal valorPoupancaEqual;
    private BigDecimal valorPoupancaEqualCusteio;

    private BigDecimal valorMCRAnual;

    private BigDecimal valorMCR62;

}
