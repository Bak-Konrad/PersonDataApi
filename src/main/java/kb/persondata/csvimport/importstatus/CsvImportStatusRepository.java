package kb.persondata.csvimport.importstatus;

import kb.persondata.csvimport.importstatus.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvImportStatusRepository extends JpaRepository<ImportStatus, Long> {

}
