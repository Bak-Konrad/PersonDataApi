package kb.persondata.employee.strategy;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import kb.persondata.employee.model.Employee;
import kb.persondata.employee.model.dto.EmployeeDto;
import kb.persondata.mapper.GeneralMapper;
import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import kb.persondata.person.utils.JointFieldsUpdater;
import kb.persondata.position.model.Position;
import kb.persondata.position.model.dto.PositionDto;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("EMPLOYEE")
@AllArgsConstructor
public class EmployeeHandlingStrategy implements PersonHandlingStrategy<Employee> {
    private final JointFieldsUpdater fieldsUpdater;
    private final GeneralMapper generalMapper;

    @Override
    public String entityType() {
        return Employee.class.getSimpleName();
    }

    @Override
    public Employee addPerson(CreatePersonCommand personCommand) {
        Map<String, String> params = personCommand.getParams();
        return Employee.builder()
                .entityType(Employee.class.getSimpleName().toUpperCase())
                .firstName(params.get("firstName"))
                .lastName(params.get("lastName"))
                .personalNumber(params.get("personalNumber"))
                .emailAddress(params.get("emailAddress"))
                .weight(Double.valueOf(params.get("weight")))
                .height(Double.valueOf(params.get("height")))
                .employmentHistory(new ArrayList<>())
                .build();
    }

    @Override
    public Person updatePerson(Employee personToUpdate, UpdatePersonCommand updatePersonCommand) {
        Map<String, String> params = updatePersonCommand.getParams();
        fieldsUpdater.updateCommonFields(personToUpdate, params);
        if (params.containsKey("actualSalary") && !params.get("actualSalary").isBlank()) {
            personToUpdate.setActualSalary(new BigDecimal(params.get("actualSalary")));
        }
        if (params.containsKey("actualPosition") && !params.get("actualPosition").isBlank()) {
            personToUpdate.setActualPosition(params.get("actualPosition"));
        }
        if (params.containsKey("actualWorkFrom") && !params.get("actualWorkFrom").isBlank()) {
            personToUpdate.setActualWorkFrom(LocalDate.parse(params.get("actualWorkFrom")));
        }
        return personToUpdate;
    }

    @Override
    public Employee addPersonFromCsv(String[] personData) {
        return Employee.builder()
                .entityType(personData[0].toUpperCase())
                .firstName(personData[1])
                .lastName(personData[2])
                .emailAddress(personData[6])
                .personalNumber(personData[3])
                .weight(Double.parseDouble(personData[5]))
                .height(Double.parseDouble(personData[4]))
                .actualWorkFrom(LocalDate.parse(personData[11]))
                .actualPosition(personData[9])
                .actualSalary(new BigDecimal(personData[10]))
                .build();
    }

    @Override
    public EmployeeDto createPersonDto(Employee person) {
        return EmployeeDto.builder()
                .id(person.getId())
                .entityType(person.getEntityType().toUpperCase())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .emailAddress(person.getEmailAddress())
                .personalNumber(person.getPersonalNumber())
                .height(person.getHeight())
                .weight(person.getWeight())
                .actualWorkFrom(person.getActualWorkFrom())
                .actualPosition(person.getActualPosition())
                .actualSalary(person.getActualSalary())
                .employmentHistory(createPositionDto(person.getEmploymentHistory()))
                .build();
    }

    private List<PositionDto> createPositionDto(List<Position> positions) {
        return positions.stream()
                .map(generalMapper::mapPositionToDto)
                .toList();
    }

    @Override
    public Specification<Person> createSpecification(Specification<Person> specification,
                                                     PersonFilteringParameters filteringParameters) {
        Map<String, String> temp = filteringParameters.getParams();

        if (temp.containsKey("actualPosition")) {
            specification = specification.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("actualPosition")),
                            "%" + temp.get("actualPosition").toLowerCase() + "%"));
        }

        if (temp.containsKey("actualSalary")) {
            specification = specification.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("actualSalary"),
                            new BigDecimal(temp.get("actualSalary"))));
        }

        if (temp.containsKey("actualWorkFrom")) {
            specification = specification.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("actualWorkFrom"),
                            LocalDate.parse(temp.get("actualWorkFrom"))));
        }

        if (temp.containsKey("employmentHistory")) {
            String employeePersonalNumber = temp.get("employmentHistory");
            specification = specification.and((root, query, builder) -> {
                Join<Employee, Position> positionJoin = root.join("employmentHistory", JoinType.LEFT);
                Join<Position, Employee> employeeJoin = positionJoin.join("employee", JoinType.LEFT);
                return builder.equal(employeeJoin.get("personalNumber"), employeePersonalNumber);
            });
        }
        return specification;
    }

    @Override
    public boolean hasChanges(UpdatePersonCommand updatePersonCommand) {
        Map<String, String> params = updatePersonCommand.getParams();
        boolean hasChanges = fieldsUpdater.hasChanges(params);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if ((key.equals("actualPosition") || key.equals("actualSalary") || key.equals("actualWorkFrom"))
                    && entry.getValue() != null && !entry.getValue().isBlank()) {
                hasChanges = true;
                break;
            }
        }
        return hasChanges;
    }

}
