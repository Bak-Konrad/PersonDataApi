package kb.persondata.person.utils;

import kb.persondata.person.model.Person;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JointFieldsUpdater {

    public void updateCommonFields(Person person, Map<String, String> params) {

        if (params.containsKey("firstName") && !params.get("firstName").isBlank()) {
            person.setFirstName(params.get("firstName"));
        }
        if (params.containsKey("lastName") && !params.get("lastName").isBlank()) {
            person.setLastName(params.get("lastName"));
        }
        if (params.containsKey("personalNumber") && !params.get("personalNumber").isBlank()) {
            person.setPersonalNumber(params.get("personalNumber"));
        }
        if (params.containsKey("height") && !params.get("height").isBlank()) {
            person.setHeight(Double.parseDouble(params.get("height")));
        }
        if (params.containsKey("weight") && !params.get("weight").isBlank()) {
            person.setWeight(Double.parseDouble(params.get("weight")));
        }
        if (params.containsKey("emailAddress") && !params.get("emailAddress").isBlank()) {
            person.setEmailAddress(params.get("emailAddress"));
        }
    }

    public boolean hasChanges(Map<String, String> params) {
        boolean changesFound = false;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if ((key.equals("firstName") || key.equals("lastName") || key.equals("personalNumber")
                    || key.equals("height") || key.equals("weight") || key.equals("emailAddress")) &&
                    entry.getValue() != null && !entry.getValue().isBlank()) {
                changesFound = true;
                break;
            }
        }
        return changesFound;
    }
}

