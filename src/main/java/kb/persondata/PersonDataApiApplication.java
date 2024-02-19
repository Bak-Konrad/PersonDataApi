package kb.persondata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PersonDataApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonDataApiApplication.class, args);
    }

}
