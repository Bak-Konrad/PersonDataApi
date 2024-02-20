package kb.persondata.position;

import jakarta.persistence.EntityNotFoundException;
import kb.persondata.employee.EmployeeRepository;
import kb.persondata.employee.model.Employee;
import kb.persondata.mapper.GeneralMapper;
import kb.persondata.position.model.Position;
import kb.persondata.position.model.dto.PositionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionService {
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final GeneralMapper generalMapper;


    public PositionDto registerPositionForEmployee(Long employeeId, Position positionToSave) {
        Employee employeeToUpdate = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Employee related to id= {0} has not been found", employeeId)));

        validatePositionDates(employeeToUpdate, positionToSave);
        positionToSave.setEmployee(employeeToUpdate);

        employeeToUpdate.getEmploymentHistory().add(positionToSave);
        log.info("REGISTER POSITION pozycje employee " + employeeToUpdate.getEmploymentHistory());
        employeeToUpdate.setActualWorkFrom(positionToSave.getFromDate());
        employeeToUpdate.setActualSalary(positionToSave.getSalary());
        employeeToUpdate.setActualPosition(positionToSave.getPositionName());

        positionRepository.save(positionToSave);
        employeeRepository.save(employeeToUpdate);
        return generalMapper.mapPositionToDto(positionToSave);
    }

    private void validatePositionDates(Employee employee, Position newPosition) {
        List<Position> employmentHistory = employee.getEmploymentHistory();

        for (Position position : employmentHistory) {
            if (position.getToDate().isAfter(newPosition.getFromDate())) {
                throw new IllegalArgumentException("New position dates cannot overlap");
            }
        }
    }
}
