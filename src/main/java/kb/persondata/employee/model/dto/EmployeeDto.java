package kb.persondata.employee.model.dto;

import kb.persondata.person.model.dto.PersonDto;
import kb.persondata.position.model.dto.PositionDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@SuperBuilder
@Getter
public class EmployeeDto extends PersonDto {
    private String actualPosition;
    private BigDecimal actualSalary;
    private LocalDate actualWorkFrom;
    private List<PositionDto> employmentHistory;
}
