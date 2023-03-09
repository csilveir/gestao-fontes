package br.com.sicredi.gestaofontes.domain;

import br.com.sicredi.gestaofontes.dto.RetornoImportacaoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImportacaoExcelService {

    private final Path basePath = Paths.get(System.getProperty("java.io.tmpdir"));


    public Mono<RetornoImportacaoDto> importarArquivo(final Mono<FilePart> filePartMono) {

        var id = UUID.randomUUID().toString();
        var newDirectory = basePath.resolve(id);

        try {
            var directory = Files.createDirectory(newDirectory);
            return filePartMono
                    .doOnNext(fp -> log.info("Received File : " + fp.filename()))
                    .flatMap(fp -> fp.transferTo(directory.resolve(fp.filename())))
                    .then(Mono.just(new RetornoImportacaoDto(id)));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
