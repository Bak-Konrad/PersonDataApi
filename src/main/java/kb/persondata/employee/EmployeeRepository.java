package kb.persondata.employee;

import jakarta.persistence.LockModeType;
import kb.persondata.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    pessimistic, bo dodawanie stanowiska raczej rzadko jest używane i nie będzie blokować api aż tak
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Employee> findWithLockById(Long employeeId);
}
