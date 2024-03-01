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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data/persons")
@Slf4j
public class PersonController {

    private final PersonService personService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PersonDto> addPerson(@Valid @RequestBody CreatePersonCommand createPersonCommand) {
        return new ResponseEntity<>(personService.addPerson(createPersonCommand), HttpStatus.CREATED);
    }


    @PatchMapping("/{personId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PersonDto> updatePerson(@PathVariable Long personId,
                                                  @Valid @RequestBody UpdatePersonCommand updatePersonCommand) {

        return new ResponseEntity<>(personService.updatePerson(personId, updatePersonCommand), HttpStatus.OK);
    }



    @GetMapping
    public ResponseEntity<Page<PersonDto>> getPeople(@RequestBody PersonFilteringParameters params,
                                                     @PageableDefault(size = 20) Pageable pageable) {

        return new ResponseEntity<>(personService.findPeople(params, pageable), HttpStatus.OK);
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @PatchMapping("/{personId}")
//    Test method for optimistic locking
//    public ResponseEntity<PersonDto> updatePerson(@PathVariable Long personId,
//                                                  @Valid @RequestBody UpdatePersonCommand updatePersonCommand) {
//        CompletableFuture<PersonDto> future1 = CompletableFuture.supplyAsync(() ->
//                personService.updatePerson(personId, updatePersonCommand), executorService);
//        CompletableFuture<PersonDto> future2 = CompletableFuture.supplyAsync(() ->
//                personService.updatePerson(personId, updatePersonCommand), executorService);
//        CompletableFuture<PersonDto> future3 = CompletableFuture.supplyAsync(() ->
//                personService.updatePerson(personId, updatePersonCommand), executorService);
//
//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);
//
//        try {
//
//            allFutures.join();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        PersonDto result1 = future1.join();
//        PersonDto result2 = future2.join();
//        PersonDto result3 = future3.join();
//
//
//        return new ResponseEntity<>(result1, HttpStatus.OK);
//    }
}
