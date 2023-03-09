package br.com.sicredi.gestaofontes.domain;

import br.com.sicredi.gestaofontes.dto.ArquivoRateioDto;
import br.com.sicredi.gestaofontes.dto.RetornoImportacaoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImportacaoExcelService {


    public static final int COLUMN_A = 0;
    @Value("${excel.inicioDocumento}")
    public int inicioDocumento;
    @Value("${excel.abaGop}")
    public int baseGop;
    @Value("${excel.extensao}")
    public String extensao;
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

    public Optional<File> encontrarArquivo(final String id) {

        var directory = basePath.resolve(id);

        if (Files.exists(directory)) {
            return Stream.of(
                            Objects.requireNonNull(directory.toFile().listFiles((dir, name)
                                    -> dir.canRead() && dir.isDirectory())))
                    .filter(file -> {
                        var excelFile = file.getAbsolutePath().toLowerCase();
                        return excelFile.contains(extensao) && !excelFile.contains("lock");
                    })
                    .findFirst();
        }

        return Optional.empty();
    }

    public Mono<List<ArquivoRateioDto>> carregarArquivo(final File excelFile) {
        try {
            log.info("Carregando o arquivo " + excelFile);
            var wb = new XSSFWorkbook(excelFile);
            return Mono.just(carregarDados(wb.getSheetAt(baseGop)));

        } catch (InvalidFormatException | IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }


    }

    private List<ArquivoRateioDto> carregarDados(final Sheet sheet) {

        var rateioDtoArrayList = new ArrayList<ArquivoRateioDto>();

        for (Row row : sheet) {
            if (row.getRowNum() >= inicioDocumento) {
                var arquivoRateioDto = new ArquivoRateioDto();
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == COLUMN_A) {
                        arquivoRateioDto.setCodigoCredis(getNumericValue(cell));
                    }
                }
                rateioDtoArrayList.add(arquivoRateioDto);
            }
        }

        return rateioDtoArrayList;


    }

    private double getNumericValue(final Cell cell) {

        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return cell.getNumericCellValue();
        }

        return 0d;
    }

}
