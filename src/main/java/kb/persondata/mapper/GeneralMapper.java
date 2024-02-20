package kb.persondata.mapper;

import kb.persondata.csvimport.importstatus.model.ImportStatus;
import kb.persondata.csvimport.importstatus.model.dto.ImportStatusDto;
import kb.persondata.position.model.Position;
import kb.persondata.position.model.command.CreatePositionCommand;
import kb.persondata.position.model.dto.PositionDto;
import org.springframework.stereotype.Service;

@Service
public class GeneralMapper {
    ///DTO
    public PositionDto mapPositionToDto(Position position) {
        return PositionDto.builder()
                .id(position.getId())
                .positionName(position.getPositionName())
                .fromDate(position.getFromDate())
                .toDate(position.getToDate())
                .salary(position.getSalary())
                .build();
    }

    public ImportStatusDto mapImportStatusToDto(ImportStatus status) {
        return ImportStatusDto.builder()
                .status(status.getStatus())
                .id(status.getStatusId())
                .creationDate(status.getCreationDate())
                .processedLines(status.getProcessedLines())
                .endDate(status.getEndDate())
                .build();
    }

    ///COMMAND
    public Position mapPositionFromCommand(CreatePositionCommand positionCommand) {
        return Position.builder()
                .positionName(positionCommand.getPositionName())
                .salary(positionCommand.getSalary())
                .toDate(positionCommand.getToDate())
                .fromDate(positionCommand.getFromDate())
                .build();
    }
}
