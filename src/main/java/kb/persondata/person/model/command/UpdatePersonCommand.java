package kb.persondata.person.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonCommand {
    private Long version;
    private String type;
    private Map<String, String> params;
}
