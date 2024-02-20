package kb.persondata.position.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class PositionDto {
    private Long id;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String positionName;
    private BigDecimal salary;
}
