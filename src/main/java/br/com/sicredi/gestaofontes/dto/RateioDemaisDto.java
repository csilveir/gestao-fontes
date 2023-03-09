package br.com.sicredi.gestaofontes.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RateioDemaisDto {

    private BigDecimal valorPoupancaEqual;
    private BigDecimal valorMCR2CatAnual;
    private BigDecimal valorLCA;

    private BigDecimal valorOutrasFontes;
}
