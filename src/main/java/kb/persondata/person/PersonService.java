package kb.persondata.person;

import jakarta.persistence.EntityNotFoundException;
import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.dto.PersonDto;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import kb.persondata.person.utils.JointFieldsQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final Map<String, PersonHandlingStrategy> handlingStrategyMap;
    private final PersonRepository personRepository;
    private final JointFieldsQueryBuilder jointFieldsQueryBuilder;

    public PersonDto addPerson(CreatePersonCommand personCommand) {
        Person personToSave = handlingStrategyMap.get(personCommand.getType()).addPerson(personCommand);
        personRepository.save(personToSave);
        return handlingStrategyMap.get(personCommand.getType()).createPersonDto(personToSave);

    }

    @Transactional
    public PersonDto updatePerson(Long personId, UpdatePersonCommand updatePersonCommand) {
        boolean hasChanges = handlingStrategyMap.get(updatePersonCommand.getType()).hasChanges(updatePersonCommand);
        if (!hasChanges) {
            throw new IllegalArgumentException("Blank update data or not exist");
        }
        Person personToUpdate = personRepository.findWithLockById(personId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Person related to id= {0} has not been found", personId)));

        handlingStrategyMap.get(personToUpdate.getEntityType()).updatePerson(personToUpdate, updatePersonCommand);

        return handlingStrategyMap.get(personToUpdate.getEntityType()).createPersonDto(personToUpdate);
    }


    public Page<PersonDto> findPeople(PersonFilteringParameters params, Pageable pageable) {
        Map<String, String> temp = params.getParams();
        if (temp.isEmpty()) {
            throw new IllegalArgumentException("Search Parameters not exist");
        }
        Specification<Person> querySpecification = jointFieldsQueryBuilder
                .createSpecification(temp);
        if (temp.containsKey("entityType")) {
            querySpecification = handlingStrategyMap.get(temp.get("entityType").toUpperCase())
                    .createSpecification(querySpecification, params);
        }
        Page<Person> personPage = personRepository.findAll(querySpecification, pageable);

        return personPage.map(person -> handlingStrategyMap.get(person.getEntityType()).createPersonDto(person));
    }
}