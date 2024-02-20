package kb.persondata.csvimport.utils;

import kb.persondata.person.PersonRepository;
import kb.persondata.person.model.Person;
import kb.persondata.person.strategy.PersonHandlingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
//KLASA DO WYCIAGNIECIA METOD ZAPISU BATCHA Z CSVIMPORTMETHOD
public class BatchingMethodForCsvAsync {
    private final Map<String, PersonHandlingStrategy> importStrategies;
    private final PersonRepository personRepository;

    //    @Transactional(propagation = Propagation.NESTED)
//    Generalnie tutaj na koniec zauwazyłem jeszcze drugą opcję - rollback całości zapisanego pliku. Zdecydowałem się
//    zostawić tak jak zrobiłem pierwotnie, bo jak zaczałem probować zagnieżdżać na szybko dziś  transakcje to nested się wywalał
//    a przy zwykłym Transactionalu hibernate trzymał paczki dopóki nie zakonczyła się cała metoda, a to chyba zabije APi
//
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBatch(List<String[]> batch) {
        List<Person> personList = new ArrayList<>();
        for (String[] row : batch) {
            String type = row[0].toUpperCase();
            personList.add(importPerson(row, type));
        }
        personRepository.saveAll(personList);
        log.info("Batch saved " + batch);
    }

    private Person importPerson(String[] line, String personType) {
        log.info(importStrategies.keySet().toString());
        log.info("LINIJKA" + Arrays.toString(line));
        return importStrategies.get(personType).addPersonFromCsv(line);
    }
}
