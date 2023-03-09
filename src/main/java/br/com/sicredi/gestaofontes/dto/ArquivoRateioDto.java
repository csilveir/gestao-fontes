package br.com.sicredi.gestaofontes.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "codigoCredis")
public class ArquivoRateioDto {

    private double codigoCredis;
}
