package kb.persondata.csvimport;

import jakarta.validation.Valid;
import kb.persondata.csvimport.importstatus.ImportStatusService;
import kb.persondata.csvimport.importstatus.model.dto.ImportStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data/csvfiles")
@Slf4j
public class CsvImportController {
    private final CsvImportProcessingService importService;
    private final ImportStatusService importStatusService;


    @PreAuthorize("hasAuthority('ROLE_ADMIN,ROLE_IMPORTER')")

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ImportStatusDto> addPersonsFromFile(@RequestPart("file") MultipartFile file,
                                                              @RequestParam String statusCommand) {
        return new ResponseEntity<>(importService.importCsv(file, statusCommand), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN,ROLE_IMPORTER')")
    @GetMapping("/{importId}")
    public ResponseEntity<ImportStatusDto> getImportStatus(@PathVariable String importId) {

        return new ResponseEntity<>(importStatusService.findById(importId), HttpStatus.OK);
    }
}