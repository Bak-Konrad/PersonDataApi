package kb.persondata.person;

import jakarta.validation.Valid;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.person.model.dto.PersonDto;
import kb.persondata.person.model.filter.PersonFilteringParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data/persons")
@Slf4j
public class PersonController {

    private final PersonService personService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PersonDto> addPerson(@Valid @RequestBody CreatePersonCommand createPersonCommand) {
        return new ResponseEntity<>(personService.addPerson(createPersonCommand), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{personId}")
    public ResponseEntity<PersonDto> updatePerson(@PathVariable Long personId,
                                                  @Valid @RequestBody UpdatePersonCommand updatePersonCommand) {
        return new ResponseEntity<>(personService.updatePerson(personId, updatePersonCommand), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PersonDto>> getPeople(@RequestBody PersonFilteringParameters params,
                                                     @PageableDefault(size = 20) Pageable pageable) {
        log.info("PARAMETRY W CONTROLLERZE" + params);

        return new ResponseEntity<>(personService.findPeople(params, pageable), HttpStatus.OK);
    }
}
