package kb.persondata.employee;

import kb.persondata.mapper.GeneralMapper;
import kb.persondata.position.PositionService;
import kb.persondata.position.model.Position;
import kb.persondata.position.model.command.CreatePositionCommand;
import kb.persondata.position.model.dto.PositionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data/employees")
public class EmployeeController {
    private final PositionService positionService;
    private final GeneralMapper generalMapper;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    @PatchMapping("/{employeeId}/positions")
    public ResponseEntity<PositionDto> addPositionForEmployee(@PathVariable Long employeeId,
                                                              @RequestBody CreatePositionCommand positionCommand) {
        Position positionToBeSaved = generalMapper.mapPositionFromCommand(positionCommand);
        return new ResponseEntity<>(positionService.registerPositionForEmployee(employeeId, positionToBeSaved)
                , HttpStatus.OK);
    }
}