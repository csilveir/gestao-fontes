package br.com.sicredi.gestaofontes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class GestaoFontesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoFontesApplication.class, args);
	}

}
