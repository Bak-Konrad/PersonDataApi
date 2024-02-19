package kb.persondata.person.model.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PersonDto {
    private Long id;
    private String entityType;
    private String firstName;
    private String lastName;
    private String personalNumber;
    private Double height;
    private Double weight;
    private String emailAddress;

}
