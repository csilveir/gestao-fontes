package br.com.sicredi.gestaofontes.api;


import br.com.sicredi.gestaofontes.domain.ImportacaoExcelService;
import br.com.sicredi.gestaofontes.dto.ArquivoRateioDto;
import br.com.sicredi.gestaofontes.dto.RetornoImportacaoDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("importacao")
@AllArgsConstructor
public class ImportacaoExcelAPI {

    private ImportacaoExcelService importacaoExcelService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RetornoImportacaoDto> handleFileUpload(@RequestPart("file") Mono<FilePart> file) throws IOException {
        return importacaoExcelService.importarArquivo(file);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<List<ArquivoRateioDto>> getFile(@PathVariable final String id) {
        var optionalFile = importacaoExcelService.encontrarArquivo(id);
        if (optionalFile.isPresent()) {
            return importacaoExcelService.carregarArquivo(optionalFile.get());
        }
        return Mono.empty();
    }

}
