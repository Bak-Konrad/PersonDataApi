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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportMethod {
    private final ImportStatusService importStatusService;
    private final BatchingMetchodForCsvAsync batchingMetchodForCsvAsync;
    @Value("${csv.batch.size}")
    private int batchSize;

    @Transactional
    public void importCsv(MultipartFile file, ImportStatus importStatus) {
        long processedLines = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withSkipLines(1)
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> batch = new ArrayList<>();
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                batch.add(line);
//                Thread.sleep(5000);
                processedLines++;
                log.info("Lines processed: {}", processedLines);

                if (batch.size() >= batchSize) {
                    batchingMetchodForCsvAsync.saveBatch(batch);
                    batch.clear();
                    importStatusService.updateProcessedLines(importStatus, processedLines);
                }
            }

            if (!batch.isEmpty()) {
                batchingMetchodForCsvAsync.saveBatch(batch);
            }
            importStatusService.updateProcessedLines(importStatus, processedLines);
        } catch (IOException | CsvValidationException e) {
            importStatusService.updateProcessedLinesForError(importStatus, processedLines);
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
//        catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            log.error("Thread interrupted while sleeping.", e);
//        }
        finally {
            importStatusService.setEndDate(importStatus);
            log.info("CSV import completed");
        }
    }
}
