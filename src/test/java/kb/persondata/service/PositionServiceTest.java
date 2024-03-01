package kb.persondata.service;

import jakarta.persistence.EntityNotFoundException;
import kb.persondata.employee.EmployeeRepository;
import kb.persondata.employee.model.Employee;
import kb.persondata.mapper.GeneralMapper;
import kb.persondata.position.PositionRepository;
import kb.persondata.position.PositionService;
import kb.persondata.position.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {
    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private GeneralMapper generalMapper;

    @InjectMocks
    private PositionService positionService;

    @Test
    public void testRegisterPositionForEmployee_Success() {
        // given
        Long employeeId = 1L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        Position positionToSave = new Position();
        positionToSave.setFromDate(fromDate);

        Employee employeeToUpdate = new Employee();
        List<Position> employmentHistory = new ArrayList<>();
        employeeToUpdate.setEmploymentHistory(employmentHistory);

        when(employeeRepository.findWithLockById(employeeId)).thenReturn(Optional.of(employeeToUpdate));

        // when
        positionService.registerPositionForEmployee(employeeId, positionToSave);

        // then
        verify(positionRepository).save(positionToSave);
        verify(employeeRepository).save(employeeToUpdate);
    }

    @Test
    public void testRegisterPositionForEmployee_EmployeeNotFound() {
        // Given
        Long employeeId = 1L;
        Position positionToSave = new Position();
        String errorMessage = "Employee related to id= 1 has not been found";

        // When + Then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> positionService.registerPositionForEmployee(employeeId, positionToSave))
                .withMessage(errorMessage);

        verify(positionRepository, never()).save(any());
    }

    @Test
    public void testRegisterPositionForEmployee_OverlappingDates() {
        // Given
        Long employeeId = 1L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 1);
        Position positionToSave = new Position();
        positionToSave.setFromDate(fromDate);
        positionToSave.setToDate(toDate);

        Position existingPosition = new Position();
        existingPosition.setFromDate(LocalDate.of(2022, 1, 1));
        existingPosition.setToDate(LocalDate.of(2023, 6, 30));

        Employee employeeToUpdate = new Employee();
        List<Position> employmentHistory = new ArrayList<>();
        employmentHistory.add(existingPosition);
        employeeToUpdate.setEmploymentHistory(employmentHistory);

        when(employeeRepository.findWithLockById(employeeId)).thenReturn(Optional.of(employeeToUpdate));

        // When + Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> positionService.registerPositionForEmployee(employeeId, positionToSave))
                .withMessage("New position dates cannot overlap");

        verify(positionRepository, never()).save(any());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void testRegisterPositionForEmployee_NewPositionBeforeExisting() {
        // Given
        Long employeeId = 1L;
        LocalDate fromDate = LocalDate.of(2022, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 1, 1);
        Position positionToSave = new Position();
        positionToSave.setFromDate(fromDate);
        positionToSave.setToDate(toDate);

        Position existingPosition = new Position();
        existingPosition.setFromDate(LocalDate.of(2023, 6, 30));
        existingPosition.setToDate(LocalDate.of(2024, 1, 1));

        Employee employeeToUpdate = new Employee();
        List<Position> employmentHistory = new ArrayList<>();
        employmentHistory.add(existingPosition);
        employeeToUpdate.setEmploymentHistory(employmentHistory);

        when(employeeRepository.findWithLockById(employeeId)).thenReturn(Optional.of(employeeToUpdate));

        // When + Then
        positionService.registerPositionForEmployee(employeeId, positionToSave); // No exception should be thrown

        verify(positionRepository).save(positionToSave);
        verify(employeeRepository).save(employeeToUpdate);
    }
    @Test
    public void testRegisterPositionForEmployee_NewPositionAfterExisting() {
        // Given
        Long employeeId = 1L;
        LocalDate fromDate = LocalDate.of(2024, 1, 2);
        LocalDate toDate = LocalDate.of(2025, 1, 1);
        Position positionToSave = new Position();
        positionToSave.setFromDate(fromDate);
        positionToSave.setToDate(toDate);

        Position existingPosition = new Position();
        existingPosition.setFromDate(LocalDate.of(2023, 6, 30));
        existingPosition.setToDate(LocalDate.of(2024, 1, 1));

        Employee employeeToUpdate = new Employee();
        List<Position> employmentHistory = new ArrayList<>();
        employmentHistory.add(existingPosition);
        employeeToUpdate.setEmploymentHistory(employmentHistory);

        when(employeeRepository.findWithLockById(employeeId)).thenReturn(Optional.of(employeeToUpdate));

        // When + Then
        positionService.registerPositionForEmployee(employeeId, positionToSave); // No exception should be thrown

        verify(positionRepository).save(positionToSave);
        verify(employeeRepository).save(employeeToUpdate);
    }

    @Test
    public void testRegisterPositionForEmployee_NewPositionSameAsExisting() {
        // Given
        Long employeeId = 1L;
        LocalDate fromDate = LocalDate.of(2023, 6, 30);
        LocalDate toDate = LocalDate.of(2024, 1, 1);
        Position positionToSave = new Position();
        positionToSave.setFromDate(fromDate);
        positionToSave.setToDate(toDate);

        Position existingPosition = new Position();
        existingPosition.setFromDate(LocalDate.of(2023, 6, 30));
        existingPosition.setToDate(LocalDate.of(2024, 1, 1));

        Employee employeeToUpdate = new Employee();
        List<Position> employmentHistory = new ArrayList<>();
        employmentHistory.add(existingPosition);
        employeeToUpdate.setEmploymentHistory(employmentHistory);

        when(employeeRepository.findWithLockById(employeeId)).thenReturn(Optional.of(employeeToUpdate));

        // When + Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> positionService.registerPositionForEmployee(employeeId, positionToSave))
                .withMessage("New position dates cannot overlap");

        verify(positionRepository, never()).save(any());
        verify(employeeRepository, never()).save(any());
    }


}
