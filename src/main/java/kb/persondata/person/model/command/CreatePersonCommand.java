package kb.persondata.person.model.command;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CreatePersonCommand {
    private String type;
    private Map<String, String> params;
}
