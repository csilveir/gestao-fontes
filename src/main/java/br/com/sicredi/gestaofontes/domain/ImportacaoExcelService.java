package br.com.sicredi.gestaofontes.domain;

import br.com.sicredi.gestaofontes.dto.*;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectory;

@Service
@Slf4j
public class ImportacaoExcelService {

    public static final int COLUMN_A = 0;
    private static final int COLUMN_C = 2;
    private static final int COLUMN_D = 3;
    private static final int COLUMN_E = 4;
    private static final int COLUMN_H = 6;
    private static final int COLUMN_F = 5;
    private static final int COLUMN_J = 9;
    private static final int COLUMN_K = 10;
    private static final int COLUMN_M = 12;

    private static final int COLUMN_N = 13;

    private static final int COLUMN_Q = 16;

    private static final int COLUMN_R = 17;

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
            var directory = createDirectory(newDirectory);
            return filePartMono
                    .doOnNext(fp -> {
                        log.info("Received File : " + fp.filename());
                    })
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

        log.info("Carregando o arquivo " + excelFile);
        try (var wb = new XSSFWorkbook(excelFile)) {
            return Mono.just(carregarDados(wb.getSheetAt(baseGop)));
        } catch (IOException | InvalidFormatException e) {
            log.error(e.getMessage(), e);
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
                    } else if (cell.getColumnIndex() == COLUMN_C) {
                        arquivoRateioDto.setNomeCooperativa(getStringValue(cell));
                    } else if (cell.getColumnIndex() >= COLUMN_D && cell.getColumnIndex() <= COLUMN_H) {
                        valoresDemaisRateio(cell, arquivoRateioDto);
                    } else if (cell.getColumnIndex() >= COLUMN_J && cell.getColumnIndex() <= COLUMN_N) {
                        valoresPronamp(cell, arquivoRateioDto);
                    } else if (cell.getColumnIndex() >= COLUMN_J && cell.getColumnIndex() <= COLUMN_N) {
                        valoresPronamp(cell, arquivoRateioDto);
                    } else if (cell.getColumnIndex() >= COLUMN_Q && cell.getColumnIndex() <= COLUMN_R) {
                        valoresPronaf(cell, arquivoRateioDto);
                    }
                }
                if (Objects.nonNull(arquivoRateioDto.getCodigoCredis()) &&
                        arquivoRateioDto.getCodigoCredis().compareTo(BigDecimal.ZERO) > 0)
                    rateioDtoArrayList.add(arquivoRateioDto);
            }
        }

        return rateioDtoArrayList;


    }

    private void valoresDemaisRateio(final Cell cell, ArquivoRateioDto arquivoRateioDto) {
        if (Objects.isNull(arquivoRateioDto.getRateioDemais())) {
            arquivoRateioDto.setRateioDemais(new RateioDemaisDto());
        }
        if (Objects.nonNull(arquivoRateioDto.getCodigoCredis())) {
            var rateioDemaisDto = arquivoRateioDto.getRateioDemais();

            switch (cell.getColumnIndex()) {
                case COLUMN_D -> rateioDemaisDto.setValorPoupancaEqual(getFormulaValueAsNumeric(cell));
                case COLUMN_E -> rateioDemaisDto.setValorMCR2CatAnual(getFormulaValueAsNumeric(cell));
                case COLUMN_F -> rateioDemaisDto.setValorLCA(getFormulaValueAsNumeric(cell));
                case COLUMN_H -> rateioDemaisDto.setValorOutrasFontes(getFormulaValueAsNumeric(cell));
            }
        }
    }

    private void valoresPronamp(final Cell cell, ArquivoRateioDto arquivoRateioDto) {
        if (Objects.isNull(arquivoRateioDto.getPronamp())) {
            arquivoRateioDto.setPronamp(new PronampDto());
        }
        if (Objects.nonNull(arquivoRateioDto.getPronamp())) {
            var pronampDto = arquivoRateioDto.getPronamp();

            switch (cell.getColumnIndex()) {
                case COLUMN_J -> pronampDto.setValorPoupancaEqual(getFormulaValueAsNumeric(cell));
                case COLUMN_K -> pronampDto.setValorPoupancaEqualCusteio(getFormulaValueAsNumeric(cell));
                case COLUMN_M -> pronampDto.setValorMCRAnual(getFormulaValueAsNumeric(cell));
                case COLUMN_N -> pronampDto.setValorMCR62(getFormulaValueAsNumeric(cell));

            }
        }
    }

    private void valoresPronaf(final Cell cell, ArquivoRateioDto arquivoRateioDto) {
        if (Objects.isNull(arquivoRateioDto.getPronaf())) {
            arquivoRateioDto.setPronaf(new PronafDto());
        }
        if (Objects.nonNull(arquivoRateioDto.getPronaf())) {
            var pronaf = arquivoRateioDto.getPronaf();

            switch (cell.getColumnIndex()) {
                case COLUMN_Q -> pronaf.setValorPoupancaEqual(getFormulaValueAsNumeric(cell));
                case COLUMN_R -> pronaf.setValorMCR62(getFormulaValueAsNumeric(cell));


            }
        }
    }

    private BigDecimal getNumericValue(final Cell cell) {

        if (cell.getCellType().equals(CellType.NUMERIC)) {
            try {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                log.error(e.getMessage(), e);
            }

        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getFormulaValueAsNumeric(final Cell cell) {

        if (cell.getCellType().equals(CellType.FORMULA)) {
            try {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                log.error(e.getMessage(), e);
            }
        }

        return BigDecimal.ZERO;
    }

    private String getStringValue(final Cell cell) {

        if (cell.getCellType().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        }

        return null;
    }

}
