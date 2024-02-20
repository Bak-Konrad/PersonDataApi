package kb.persondata.pensioner.strategy;

import kb.persondata.pensioner.model.Pensioner;
import kb.persondata.pensioner.model.dto.PensionerDto;
import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import kb.persondata.person.utils.JointFieldsUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component("PENSIONER")
@RequiredArgsConstructor
public class PensionerHandlingStrategy implements PersonHandlingStrategy<Pensioner> {
    private final JointFieldsUpdater fieldsUpdater;

    @Override
    public String entityType() {
        return Pensioner.class.getSimpleName();
    }

    @Override
    public Pensioner addPerson(CreatePersonCommand personCommand) {
        Map<String, String> params = personCommand.getParams();
        return Pensioner.builder()
                .entityType(Pensioner.class.getSimpleName().toUpperCase())
                .firstName(params.get("firstName"))
                .lastName(params.get("lastName"))
                .personalNumber(params.get("personalNumber"))
                .emailAddress(params.get("emailAddress"))
                .weight(Double.valueOf(params.get("weight")))
                .height(Double.valueOf(params.get("height")))
                .yearsWorked(Integer.parseInt(params.get("yearsWorked")))
                .pensionValue(new BigDecimal(params.get("pensionValue")))
                .build();
    }

    @Override
    public Person updatePerson(Pensioner personToUpdate, UpdatePersonCommand updatePersonCommand) {

        Map<String, String> params = updatePersonCommand.getParams();
        fieldsUpdater.updateCommonFields(personToUpdate, params);
        if (params.containsKey("pensionValue") && !params.get("pensionValue").isBlank()) {
            personToUpdate.setPensionValue(new BigDecimal(params.get("pensionValue")));
        }
        if (params.containsKey("yearsWorked") && !params.get("yearsWorked").isBlank()) {
            personToUpdate.setYearsWorked((Integer.parseInt(params.get("yearsWorked"))));
        }
        return personToUpdate;
    }

    @Override
    public Pensioner addPersonFromCsv(String[] personData) {
        return Pensioner.builder()
                .entityType(personData[0].toUpperCase())
                .firstName(personData[1])
                .lastName(personData[2])
                .emailAddress(personData[6])
                .personalNumber(personData[3])
                .weight(Double.parseDouble(personData[5]))
                .height(Double.parseDouble(personData[4]))
                .pensionValue(new BigDecimal(personData[12]))
                .yearsWorked(Integer.parseInt(personData[13]))
                .build();
    }

    @Override
    public PensionerDto createPersonDto(Pensioner person) {
        return PensionerDto.builder()
                .id(person.getId())
                .entityType(person.getEntityType().toUpperCase())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .emailAddress(person.getEmailAddress())
                .personalNumber(person.getPersonalNumber())
                .height(person.getHeight())
                .weight(person.getWeight())
                .pensionValue(person.getPensionValue())
                .yearsWorked(person.getYearsWorked())
                .build();
    }

    @Override
    public Specification<Person> createSpecification(Specification<Person> specification,
                                                     PersonFilteringParameters filteringParameters) {
        Map<String, String> temp = filteringParameters.getParams();

        if (temp.containsKey("fromYearsWorked") && temp.containsKey("toYearsWorked")) {
            specification = specification.and((root, query, builder) ->
                    builder.between(root.get("yearsWorked"),
                            Integer.parseInt(temp.get("fromYearsWorked")),
                            Integer.parseInt(temp.get("toYearsWorked"))));
        }
        if (temp.containsKey("fromPensionValue") && temp.containsKey("toPensionValue")) {
            specification = specification.and((root, query, builder) ->
                    builder.between(root.get("pensionValue"),
                            Double.parseDouble(temp.get("fromPensionValue")),
                            Double.parseDouble(temp.get("toPensionValue"))));
        }
        return specification;
    }

    @Override
    public boolean hasChanges(UpdatePersonCommand updatePersonCommand) {
        Map<String, String> params = updatePersonCommand.getParams();
        boolean hasChanges = fieldsUpdater.hasChanges(params);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if ((key.equals("pensionValue") || key.equals("yearsWorked")) && entry.getValue() != null
                    && !entry.getValue().isBlank()) {
                hasChanges = true;
                break;
            }
        }
        return hasChanges;
    }
}

