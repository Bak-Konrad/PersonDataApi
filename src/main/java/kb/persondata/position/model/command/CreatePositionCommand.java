package kb.persondata.position.model.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class CreatePositionCommand {
    @NotNull(message = "From Date cannot be null")
    private LocalDate fromDate;

    private LocalDate toDate;

    @NotEmpty(message = "Position Name cannot be empty")
    private String positionName;

    @NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0.0")
    private BigDecimal salary;
}
