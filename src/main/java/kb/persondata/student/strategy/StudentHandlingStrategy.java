package kb.persondata.student.strategy;

import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import kb.persondata.person.utils.JointFieldsUpdater;
import kb.persondata.student.model.Student;
import kb.persondata.student.model.dto.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Component("STUDENT")
@RequiredArgsConstructor
public class StudentHandlingStrategy implements PersonHandlingStrategy<Student> {
    private final JointFieldsUpdater fieldsUpdater;


    @Override
    public Student addPerson(CreatePersonCommand personCommand) {
        Map<String, String> params = personCommand.getParams();
        return Student.builder()
                .entityType(Student.class.getSimpleName().toUpperCase())
                .firstName(params.get("firstName"))
                .lastName(params.get("lastName"))
                .personalNumber(params.get("personalNumber"))
                .emailAddress(params.get("emailAddress"))
                .weight(Double.valueOf(params.get("weight")))
                .height(Double.valueOf(params.get("height")))
                .scholarship(new BigDecimal(params.get("scholarship")))
                .academicYear(LocalDate.parse(params.get("academicYear")))
                .courseName(params.get("courseName"))
                .universityName(params.get("universityName"))
                .build();

    }

    @Override
    public void updatePerson(Student personToUpdate, UpdatePersonCommand updatePersonCommand) {
        Map<String, String> params = updatePersonCommand.getParams();
        fieldsUpdater.updateCommonFields(personToUpdate, params);
        if (params.containsKey("scholarship") && !params.get("scholarship").isBlank()) {
            personToUpdate.setScholarship(new BigDecimal(params.get("scholarship")));
        }
        if (params.containsKey("courseName") && !params.get("courseName").isBlank()) {
            personToUpdate.setCourseName((params.get("courseName")));
        }
        if (params.containsKey("academicYear") && !params.get("academicYear").isBlank()) {
            personToUpdate.setAcademicYear(LocalDate.parse(params.get("academicYear")));
        }
    }

    @Override
    public StudentDto createPersonDto(Student person) {
        return StudentDto.builder()
                .entityType(person.getEntityType())
                .version(person.getVersion())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .emailAddress(person.getEmailAddress())
                .personalNumber(person.getPersonalNumber())
                .height(person.getHeight())
                .weight(person.getWeight())
                .academicYear(person.getAcademicYear())
                .courseName(person.getCourseName())
                .universityName(person.getUniversityName())
                .scholarship(person.getScholarship())
                .id(person.getId())
                .build();

    }

    @Override
    public Specification<Person> createSpecification(Specification<Person> specification,
                                                     PersonFilteringParameters filteringParameters) {
        Map<String, String> temp = filteringParameters.getParams();

        if (temp.containsKey("universityName")) {
            specification = specification.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("universityName")), "%" + temp.get("universityName").toLowerCase() + "%"));
        }

        if (temp.containsKey("fromAcademicYear") && temp.containsKey("toAcademicYear")) {
            specification = specification.and((root, query, builder) ->
                    builder.between(root.get("academicYear"),
                            LocalDate.parse(temp.get("fromAcademicYear")),
                            LocalDate.parse(temp.get("toAcademicYear"))));
        } else if (temp.containsKey("academicYear")) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("academicYear"), LocalDate.parse(temp.get("academicYear"))));
        }

        if (temp.containsKey("courseName")) {
            specification = specification.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("courseName")), "%" + temp.get("courseName").toLowerCase() + "%"));
        }

        if (temp.containsKey("fromScholarship") && temp.containsKey("toScholarship")) {
            specification = specification.and((root, query, builder) ->
                    builder.between(root.get("scholarship"),
                            new BigDecimal(temp.get("fromScholarship")),
                            new BigDecimal(temp.get("toScholarship"))));
        }

        return specification;
    }

    @Override
    public boolean hasChanges(UpdatePersonCommand updatePersonCommand) {
        Map<String, String> params = updatePersonCommand.getParams();
        boolean hasChanges = fieldsUpdater.hasChanges(params);


        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if ((key.equals("universityName") || key.equals("academicYear") || key.equals("courseName") ||
                    key.equals("scholarship")) && entry.getValue() != null && !entry.getValue().isBlank()) {
                hasChanges = true;
                break;
            }
        }
        return hasChanges;
    }
}

