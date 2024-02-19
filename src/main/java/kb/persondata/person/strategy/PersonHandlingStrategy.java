package kb.persondata.person.strategy;

import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.dto.PersonDto;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import org.springframework.data.jpa.domain.Specification;

public interface PersonHandlingStrategy <T extends Person> {

    String entityType();

    Person addPerson(CreatePersonCommand personCommand);

    Person updatePerson(T person, UpdatePersonCommand updatePersonCommand);


    Person addPersonFromCsv(String[] personData);
    PersonDto createPersonDto(T person);

    Specification<Person> createSpecification(Specification<Person> specification,
                                              PersonFilteringParameters filteringParameters);

    boolean hasChanges(UpdatePersonCommand updatePersonCommand);
}
