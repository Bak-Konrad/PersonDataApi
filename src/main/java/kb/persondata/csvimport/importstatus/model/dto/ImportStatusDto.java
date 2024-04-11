package kb.persondata.csvimport.importstatus.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ImportStatusDto {
    private Long id;
    private String status;
    private final LocalDateTime creationDate;
    private LocalDateTime endDate;
    private long processedLines;

}
