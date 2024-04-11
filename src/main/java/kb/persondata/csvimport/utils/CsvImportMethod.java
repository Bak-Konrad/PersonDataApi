package kb.persondata.csvimport.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import kb.persondata.csvimport.importstatus.ImportStatusService;
import kb.persondata.csvimport.importstatus.model.ImportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportMethod {
    private final ImportStatusService importStatusService;
    private final JdbcBatchingMethodForCsvAsync jdbcBatchingMethodForCsvAsync;

    @Transactional
    public void importCsv(MultipartFile file, ImportStatus importStatus) {
        LocalDateTime startDate = LocalDateTime.now();
        List<String[]> batch = new ArrayList<>();
        long processedLines = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withSkipLines(1)
                    .withCSVParser(csvParser)
                    .build();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                batch.add(line);
                processedLines++;

                if (batch.size() >= 500) {
                    jdbcBatchingMethodForCsvAsync.saveBatch(batch);
                    batch.clear();
                }
                if (processedLines % 1000 == 0) {
                    importStatusService.updateProcessedLines(importStatus, processedLines);
                }
            }
            if (!batch.isEmpty()) {
                jdbcBatchingMethodForCsvAsync.saveBatch(batch);
            }
            importStatusService.updateProcessedLines(importStatus, processedLines);
        } catch (IOException | CsvValidationException e) {
            importStatusService.updateProcessedLinesForError(importStatus, processedLines);
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        } finally {
            importStatusService.setEndDate(importStatus);
            log.info("CSV import completed");
        }
        LocalDateTime endDate = LocalDateTime.now();
        log.info("CSV IMPORT DURATION TIME: " + ChronoUnit.MILLIS.between(startDate, endDate));
    }
}
