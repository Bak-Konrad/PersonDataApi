package kb.persondata.student.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kb.persondata.person.model.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Student extends Person {

    @NotBlank(message = "University name cannot be blank")
    @Size(min = 1, max = 50, message = "University name must be between 1 and 50 characters")
    private String universityName;

    private LocalDate academicYear;

    @NotBlank(message = "Course name cannot be blank")
    @Size(min = 1, max = 50, message = "Course name must be between 1 and 50 characters")
    private String courseName;

    @Positive(message = "Scholarship must be positive")
    private BigDecimal scholarship;
}
