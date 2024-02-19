package kb.persondata.person.utils;

import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.UpdatePersonCommand;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
//    private Long version;
//    private String entityType;
//    private String firstName;
//    private String lastName;
//
//    private String personalNumber;
//    private Double height;
//    private Double weight;
//    private String emailAddress;
@Service
public class JointFieldsUpdater {

    public Person updateCommonFields(Person person, Map<String, String> params) {

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
        return person;
    }

        public boolean hasChanges(Map<String, String> params) {
            boolean changesFound = false;

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                if ((key.equals("firstName") || key.equals("lastName") || key.equals("personalNumber")
                        || key.equals("height") || key.equals("weight") || key.equals("emailAddress")) && entry.getValue() != null) {
                    changesFound = true;
                    break;
                }
            }

            return changesFound;
        }
//
//        if (params.containsKey("firstName") && params.get("firstName") != null && !params.get("firstName").isEmpty()) {
//            person.setFirstName(params.get("firstName"));
//        }
//        if (params.containsKey("lastName") && params.get("lastName") != null && !params.get("lastName").isEmpty()) {
//            person.setLastName(params.get("lastName"));
//        }
//        if (params.containsKey("personalNumber") && params.get("personalNumber") != null && !params.get("personalNumber").isEmpty()) {
//            person.setPersonalNumber(params.get("personalNumber"));
//        }
//        if (params.containsKey("height") && params.get("height") != null && !params.get("height").isEmpty()) {
//            person.setHeight(Double.parseDouble(params.get("height")));
//        }
//        if (params.containsKey("weight") && params.get("weight") != null && !params.get("weight").isEmpty()) {
//            person.setWeight(Double.parseDouble(params.get("weight")));
//        }
//        if (params.containsKey("emailAddress") && params.get("emailAddress") != null && !params.get("emailAddress").isEmpty()) {
//            person.setEmailAddress(params.get("emailAddress"));
//        }
//        return person;

    }

