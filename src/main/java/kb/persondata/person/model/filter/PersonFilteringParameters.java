package kb.persondata.person.model.filter;

import lombok.Data;

import java.util.Map;
@Data
public class PersonFilteringParameters {
    private Map<String, String> params;
}
