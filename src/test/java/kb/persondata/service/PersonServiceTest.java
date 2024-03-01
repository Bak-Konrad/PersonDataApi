package kb.persondata.service;

import jakarta.persistence.EntityNotFoundException;
import kb.persondata.employee.model.Employee;
import kb.persondata.pensioner.model.Pensioner;
import kb.persondata.person.PersonRepository;
import kb.persondata.person.PersonService;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.dto.PersonDto;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import kb.persondata.student.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    private Map<String, PersonHandlingStrategy> handlingStrategyMap;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    public void testUpdatePerson_Employee_Success() {
        // Given
        Long personId = 1L;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand();
        updatePersonCommand.setType("Employee");
        updatePersonCommand.setParams(Map.of("weight", "200.00"));
        Employee employeeToUpdate = new Employee();
        employeeToUpdate.setEntityType("Employee");
        employeeToUpdate.setId(personId);
        employeeToUpdate.setHeight(140.00d);
        PersonDto expectedDto = PersonDto.builder()
                .weight(200.00d)
                .build();

        when(handlingStrategyMap.get("Employee")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Employee").hasChanges(updatePersonCommand)).thenReturn(true);
        when(personRepository.findById(personId)).thenReturn(java.util.Optional.of(employeeToUpdate));
        when(handlingStrategyMap.get("Employee").createPersonDto(employeeToUpdate)).thenReturn(expectedDto);

        // When
        PersonDto result = personService.updatePerson(personId, updatePersonCommand);

        // Then
        assertSame(expectedDto, result);
    }

    @Test
    public void testUpdatePerson_Pensioner_Success() {
        // Given
        Long personId = 1L;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand();
        updatePersonCommand.setType("Pensioner");
        updatePersonCommand.setParams(Map.of("lastName", "Kowalski"));
        Pensioner pensionerToUpdate = new Pensioner();
        pensionerToUpdate.setEntityType("Pensioner");
        pensionerToUpdate.setId(personId);
        pensionerToUpdate.setLastName("Kaczor");
        PersonDto expectedDto = PersonDto.builder()
                .lastName("Kowalski")
                .build();

        when(handlingStrategyMap.get("Pensioner")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Pensioner").hasChanges(updatePersonCommand)).thenReturn(true);
        when(personRepository.findById(personId)).thenReturn(java.util.Optional.of(pensionerToUpdate));
        when(handlingStrategyMap.get("Pensioner").createPersonDto(pensionerToUpdate)).thenReturn(expectedDto);

        // When
        PersonDto result = personService.updatePerson(personId, updatePersonCommand);

        // Then
        assertSame(expectedDto, result);

    }

    @Test
    public void testUpdatePerson_Student_Success() {
        // Given
        Long personId = 1L;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand();
        updatePersonCommand.setType("Student");
        updatePersonCommand.setParams(Map.of("height", "200.00"));
        Student studentToUpdate = new Student();
        studentToUpdate.setEntityType("Student");
        studentToUpdate.setId(personId);
        studentToUpdate.setHeight(120.00d);
        PersonDto expectedDto = PersonDto.builder().build();

        when(handlingStrategyMap.get("Student")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Student").hasChanges(updatePersonCommand)).thenReturn(true);
        when(personRepository.findById(personId)).thenReturn(java.util.Optional.of(studentToUpdate));
        when(handlingStrategyMap.get("Student").createPersonDto(studentToUpdate)).thenReturn(expectedDto);

        // When
        PersonDto result = personService.updatePerson(personId, updatePersonCommand);

        // Then
        assertSame(expectedDto, result);
    }

    @Test
    public void testUpdatePerson_BlankUpdateDataOrNotExist() {
        // Given
        Long personId = 1L;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand();
        updatePersonCommand.setType("Student");
        Student studentToUpdate = new Student(); // Zakładam, że Student rozszerza klasę Person

        when(handlingStrategyMap.get("Student")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Student").hasChanges(updatePersonCommand)).thenReturn(false);

        // When + Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> personService.updatePerson(personId, updatePersonCommand))
                .withMessage("Blank update data or not exist");

        verify(personRepository, never()).save(any());
    }

    @Test
    public void testUpdatePerson_EntityNotFound() {
        // Given
        Long personId = 1L;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand();
        updatePersonCommand.setType("Student");
        updatePersonCommand.setParams(Map.of("height", "200.00"));
        when(handlingStrategyMap.get("Student")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Student").hasChanges(updatePersonCommand)).thenReturn(true);
        when(personRepository.findById(personId)).thenReturn(java.util.Optional.empty());

        // When + Then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> personService.updatePerson(personId, updatePersonCommand))
                .withMessage("Person related to id= 1 has not been found");

        verify(personRepository, never()).save(any());
    }

    @Test
    public void testAddPerson_Employee_Success() {
        // Given
        CreatePersonCommand personCommand = CreatePersonCommand.builder()
                .type("Employee")
                .build();
        Employee employeeToSave = Employee.builder()
                .personalNumber("123456789")
                .build();
        PersonDto expectedDto = PersonDto.builder()
                .personalNumber("123456789")
                .build();

        when(handlingStrategyMap.get("Employee")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Employee").addPerson(personCommand)).thenReturn(employeeToSave);
        when(handlingStrategyMap.get("Employee").createPersonDto(employeeToSave)).thenReturn(expectedDto);

        // When
        PersonDto result = personService.addPerson(personCommand);

        // Then
        assertSame(expectedDto, result);
        verify(personRepository).save(employeeToSave);
    }

    @Test
    public void testAddPerson_Pensioner_Success() {
        // Given
        CreatePersonCommand personCommand = CreatePersonCommand.builder()
                .type("Pensioner")
                .build();
        Pensioner pensionerToSave = Pensioner.builder()
                .firstName("Zenon")
                .build();
        PersonDto expectedDto = PersonDto.builder()
                .firstName("Zenon")
                .build();

        when(handlingStrategyMap.get("Pensioner")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Pensioner").addPerson(personCommand)).thenReturn(pensionerToSave);
        when(handlingStrategyMap.get("Pensioner").createPersonDto(pensionerToSave)).thenReturn(expectedDto);

        // When
        PersonDto result = personService.addPerson(personCommand);

        // Then
        assertSame(expectedDto, result);
        verify(personRepository).save(pensionerToSave);
    }

    @Test
    public void testAddPerson_Student_Success() {
        // Given
        CreatePersonCommand personCommand = CreatePersonCommand.builder()
                .type("Student")
                .build();
        Student studentToSave = Student.builder()
                .lastName("Kowalski")
                .build();
        PersonDto expectedDto = PersonDto.builder()
                .lastName("Kowalski")
                .build();

        when(handlingStrategyMap.get("Student")).thenReturn(mock(PersonHandlingStrategy.class));
        when(handlingStrategyMap.get("Student").addPerson(personCommand)).thenReturn(studentToSave);
        when(handlingStrategyMap.get("Student").createPersonDto(studentToSave)).thenReturn(expectedDto);

        // When
        PersonDto result = personService.addPerson(personCommand);

        // Then
        assertSame(expectedDto, result);
        verify(personRepository).save(studentToSave);
    }


}
