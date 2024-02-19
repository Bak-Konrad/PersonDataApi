package kb.persondata.pensioner.model.dto;

import kb.persondata.person.model.dto.PersonDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
@SuperBuilder
@Getter
public class PensionerDto extends PersonDto {
    private BigDecimal pensionValue;
    private int yearsWorked;
}
