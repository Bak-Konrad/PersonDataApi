package kb.persondata.csvimport;

import kb.persondata.csvimport.importstatus.ImportStatusService;
import kb.persondata.csvimport.importstatus.model.ImportStatus;
import kb.persondata.csvimport.importstatus.model.dto.ImportStatusDto;
import kb.persondata.csvimport.utils.CsvAsyncMethod;
import kb.persondata.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvImportProcessingService {
    private final ImportStatusService importStatusService;
    private final CsvAsyncMethod csvAsyncMethod;
    private final GeneralMapper generalMapper;

    public ImportStatusDto importCsv(MultipartFile file) {
        try {
            ImportStatus status = importStatusService.createImportStatus();
            csvAsyncMethod.importCsv(file, status);
            return generalMapper.mapImportStatusToDto(status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


