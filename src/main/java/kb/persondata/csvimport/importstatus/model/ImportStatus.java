package kb.persondata.csvimport.importstatus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportStatus {
    @Id
    private String statusId;
    private String status;
    private  LocalDateTime creationDate;
    private LocalDateTime endDate;
    private long processedLines;
}
