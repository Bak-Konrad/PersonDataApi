package kb.persondata.csvimport.importstatus;

import kb.persondata.csvimport.importstatus.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CsvImportStatusRepository extends JpaRepository<ImportStatus,String> {


   Optional <ImportStatus> findByStatusId(String id);
}
