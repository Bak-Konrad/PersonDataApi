package kb.persondata.person;

import jakarta.persistence.EntityNotFoundException;
import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.dto.PersonDto;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import kb.persondata.person.utils.JointFieldsQueryBuilder;
import kb.persondata.person.utils.JointFieldsUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final Map<String, PersonHandlingStrategy> handlingStrategyMap;
    private final PersonRepository personRepository;
    private final JointFieldsQueryBuilder jointFieldsQueryBuilder;


    public PersonDto addPerson(CreatePersonCommand personCommand){
        Person personToSave = handlingStrategyMap.get(personCommand.getType()).addPerson(personCommand);
        personRepository.save(personToSave);
        return handlingStrategyMap.get(personCommand.getType()).createPersonDto(personToSave);

    }


    public PersonDto updatePerson(Long personId, UpdatePersonCommand updatePersonCommand) {
//        JointFieldsUpdater updater = new JointFieldsUpdater();
        boolean hasChanges = handlingStrategyMap.get(updatePersonCommand.getType()).hasChanges(updatePersonCommand);
//        boolean hasChanges = updater.hasChanges(updatePersonCommand.getParams());

        if (!hasChanges) {
            throw new IllegalArgumentException("Blank update data or not exist");
        }
        Person personToUpdate = personRepository.findById(personId)
                .orElseThrow(()-> new EntityNotFoundException(MessageFormat
                        .format("Person related to id= {0} has not been found", personId)));
        handlingStrategyMap.get(personToUpdate.getEntityType()).updatePerson(personToUpdate,updatePersonCommand);
        personRepository.save(personToUpdate);
        return handlingStrategyMap.get(personToUpdate.getEntityType()).createPersonDto(personToUpdate);
    }
    public Page<PersonDto> findPeople(PersonFilteringParameters params, Pageable pageable) {
        Map<String,String> temp = params.getParams();
        Specification<Person> querySpecification = jointFieldsQueryBuilder
                .createSpecification(temp);
        if(temp.containsKey("entityType")){
            querySpecification = handlingStrategyMap.get(temp.get("entityType").toUpperCase())
                    .createSpecification(querySpecification,params);
        }
        Page<Person> personPage = personRepository.findAll(querySpecification, pageable);

        log.info("ENTITY TYPPE Z PAGE " + personPage.map(Person::getEntityType).stream().collect(Collectors.toSet()));

        return personPage.map(person -> handlingStrategyMap.get(person.getEntityType()).createPersonDto(person));
    }


}
