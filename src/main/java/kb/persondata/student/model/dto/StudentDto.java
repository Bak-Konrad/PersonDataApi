package kb.persondata.student.model.dto;

import kb.persondata.person.model.dto.PersonDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SuperBuilder
@Getter
public class StudentDto extends PersonDto {
    private String universityName;
    private LocalDate academicYear;
    private String courseName;
    private BigDecimal scholarship;
}
