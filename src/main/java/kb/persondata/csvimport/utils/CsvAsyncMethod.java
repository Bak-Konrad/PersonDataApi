package kb.persondata.csvimport.utils;

import kb.persondata.csvimport.importstatus.model.ImportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CsvAsyncMethod {
    private final CsvImportMethod method;

    @Async("asyncSinglePool")
    public void importCsv(MultipartFile multipartFile, ImportStatus status) {
        try {
            method.importCsv(multipartFile, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
