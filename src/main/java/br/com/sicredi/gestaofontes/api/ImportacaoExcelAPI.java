package br.com.sicredi.gestaofontes.api;


import br.com.sicredi.gestaofontes.domain.ImportacaoExcelService;
import br.com.sicredi.gestaofontes.dto.RetornoImportacaoDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("importacao")
@AllArgsConstructor
public class ImportacaoExcelAPI {

    private ImportacaoExcelService importacaoExcelService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RetornoImportacaoDto> handleFileUpload(@RequestPart("file") Mono<FilePart> file) throws IOException {
        return importacaoExcelService.importarArquivo(file);

    }


}
