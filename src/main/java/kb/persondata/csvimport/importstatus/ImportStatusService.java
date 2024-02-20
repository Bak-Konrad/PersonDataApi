package kb.persondata.csvimport.importstatus;

import jakarta.persistence.EntityNotFoundException;
import kb.persondata.csvimport.importstatus.model.ImportStatus;
import kb.persondata.csvimport.importstatus.model.dto.ImportStatusDto;
import kb.persondata.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportStatusService {
    private final GeneralMapper generalMapper;
    private final CsvImportStatusRepository importStatusRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportStatus createImportStatus(String statusId) {
        ImportStatus importStatus = ImportStatus.builder()
                .creationDate(LocalDateTime.now())
                .statusId(statusId)
                .status("In progress")
                .build();
        importStatusRepository.save(importStatus);
        return importStatus;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProcessedLines(ImportStatus status, Long lines) {
        ImportStatus importStatusToUpdate = getFromDb(status.getStatusId());
        importStatusToUpdate.setProcessedLines(lines);
        importStatusRepository.save(importStatusToUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProcessedLinesForError(ImportStatus status, Long lines) {
        ImportStatus importStatusToUpdate = getFromDb(status.getStatusId());
        importStatusToUpdate.setProcessedLines(lines);
        importStatusToUpdate.setStatus("Error occurred");
        importStatusRepository.save(importStatusToUpdate);
    }

    public ImportStatusDto findById(String statusId) {
        ImportStatus importStatus = getFromDb(statusId);
        return generalMapper.mapImportStatusToDto(importStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setEndDate(ImportStatus status) {
        ImportStatus importStatus = getFromDb(status.getStatusId());
        importStatus.setEndDate(LocalDateTime.now());
        importStatus.setStatus("Ended");
        importStatusRepository.save(importStatus);
    }

    private ImportStatus getFromDb(String statusId) {
        return importStatusRepository.findByStatusId(statusId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("ImportStatus related to id= {0} has not been found", statusId)));
    }
}
