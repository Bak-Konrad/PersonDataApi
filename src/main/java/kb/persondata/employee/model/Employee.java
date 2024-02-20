package kb.persondata.employee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import kb.persondata.person.model.Person;
import kb.persondata.position.model.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Employee extends Person {
    @Size(min = 1, max = 50, message = "Actual position must be between 1 and 50 characters")
    private String actualPosition;
    @DecimalMin(value = "0.0", message = "Actual salary must be greater than or equal to 0")
    private BigDecimal actualSalary;
    private LocalDate actualWorkFrom;
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
    private List<Position> employmentHistory;
}
