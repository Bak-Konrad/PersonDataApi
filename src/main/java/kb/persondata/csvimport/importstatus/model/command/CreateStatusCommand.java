package kb.persondata.csvimport.importstatus.model.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateStatusCommand {
    @NotEmpty(message = "ImportStatus cannot be empty")
    private String importStatusId;

}
