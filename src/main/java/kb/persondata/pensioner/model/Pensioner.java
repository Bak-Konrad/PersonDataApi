package kb.persondata.pensioner.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kb.persondata.person.model.Person;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Pensioner extends Person {

    @NotNull(message = "Pension value cannot be null")
    @Positive
    private BigDecimal pensionValue;

    @Positive
    private int yearsWorked;
}
