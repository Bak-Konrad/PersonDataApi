package kb.persondata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kb.persondata.employee.model.Employee;
import kb.persondata.pensioner.model.Pensioner;
import kb.persondata.person.PersonRepository;
import kb.persondata.person.PersonService;
import kb.persondata.person.model.Person;
import kb.persondata.person.model.command.CreatePersonCommand;
import kb.persondata.person.model.command.UpdatePersonCommand;
import kb.persondata.student.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;
    private Pensioner pensioner;
    private Student student;

    @BeforeEach
    void setUp() {

        pensioner = Pensioner.builder()
                .entityType("PENSIONER")
                .Id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .personalNumber("98765435101")
                .height(170.0)
                .weight(75.0)
                .emailAddress("jane.smith@eghghxample.com")
                .pensionValue(new BigDecimal("1100.0"))
                .yearsWorked(35)
                .build();

        employee = Employee.builder()
                .entityType("EMPLOYEE")
                .Id(1L)
                .firstName("John")
                .lastName("Doe")
                .personalNumber("12346678901")
                .height(180.5)
                .weight(75.0)
                .emailAddress("john.doe@ex6am2ple.com")
                .build();

        student = Student.builder()
                .entityType("STUDENT")
                .Id(3L)
                .firstName("Alice")
                .lastName("Smith")
                .personalNumber("12345678234")
                .height(160.0)
                .weight(55.0)
                .emailAddress("alice.smith@example.com")
                .universityName("Example University")
                .academicYear(LocalDate.parse("2023-09-01"))
                .courseName("Computer Science")
                .scholarship(new BigDecimal("1000.0"))
                .build();
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdateEmployee() throws Exception {

        Employee saved = personRepository.saveAndFlush(employee);
        long personId = saved.getId();

        Map<String, String> employeeMap = new HashMap<>();
        employeeMap.put("firstName", "John");
        employeeMap.put("lastName", "Dudeson");

        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("EMPLOYEE")
                .params(employeeMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Dudeson"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdateEmployee_AdminRole_BlankUpdateData_NotExist() throws Exception {
        long personId = 1L;

        personRepository.saveAndFlush(employee);

        Map<String, String> emptyMap = Collections.emptyMap();

        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("EMPLOYEE")
                .params(emptyMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Blank update data or not exist"));
    }


    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdatePensioner() throws Exception {

        Pensioner pensioner1 = personRepository.saveAndFlush(pensioner);
        long personId = pensioner1.getId();

        Map<String, String> pensionerMap = new HashMap<>();
        pensionerMap.put("firstName", "Jane");
        pensionerMap.put("lastName", "Brown");

        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("PENSIONER")
                .params(pensionerMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Brown"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdatePensionerWithVersioning() throws Exception {

        Pensioner savedPensioner = personRepository.saveAndFlush(pensioner);
        long personId = savedPensioner.getId();

        long previousVersion = savedPensioner.getVersion();

        Map<String, String> pensionerMap = new HashMap<>();
        pensionerMap.put("firstName", "Jane");
        pensionerMap.put("lastName", "Brown");

        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("PENSIONER")
                .params(pensionerMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Brown"));

        Person updatedPensioner = personRepository.findById(personId).orElse(null);

        assertNotNull(updatedPensioner);
        assertEquals(previousVersion + 1, updatedPensioner.getVersion());

    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdatePensioner_AdminRole_BlankUpdateData_NotExist() throws Exception {
        long personId = 2L;

        personRepository.saveAndFlush(pensioner);

        Map<String, String> emptyMap = Collections.emptyMap();
        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("PENSIONER")
                .params(emptyMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Blank update data or not exist"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdateStudent() throws Exception {

        Student student1 = personRepository.saveAndFlush(student);
        long personId = student1.getId();

        Map<String, String> studentMap = new HashMap<>();
        studentMap.put("firstName", "Alice");
        studentMap.put("lastName", "Brown");

        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("STUDENT")
                .params(studentMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdateStudent_AdminRole_BlankUpdateData_NotExist() throws Exception {
        long personId = 3L;
        personRepository.saveAndFlush(student);

        Map<String, String> emptyMap = Collections.emptyMap();

        UpdatePersonCommand updatePersonCommand = UpdatePersonCommand.builder()
                .type("STUDENT")
                .params(emptyMap)
                .build();

        mockMvc.perform(patch("/api/v1/data/persons/{personId}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonCommand)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Blank update data or not exist"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddEmployee() throws Exception {
        Map<String, String> employeeMap = new HashMap<>();
        employeeMap.put("firstName", "John");
        employeeMap.put("lastName", "Doe");
        employeeMap.put("personalNumber", "12346678901");
        employeeMap.put("height", "180.5");
        employeeMap.put("weight", "75.0");
        employeeMap.put("emailAddress", "john.doe@ex6am2ple.com");

        CreatePersonCommand employeeCommand = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .params(employeeMap)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entityType").value("EMPLOYEE"))
                .andExpect(jsonPath("$.weight").value(75.0))
                .andExpect(jsonPath("$.height").value(180.5))
                .andExpect(jsonPath("$.emailAddress").value("john.doe@ex6am2ple.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddEmployee_InvalidEmailAddressValidationFailure() throws Exception {
        Map<String, String> employeeMap = new HashMap<>();
        employeeMap.put("firstName", "John");
        employeeMap.put("lastName", "Doe");
        employeeMap.put("personalNumber", "12346678901");
        employeeMap.put("height", "180.5");
        employeeMap.put("weight", "75.0");
        employeeMap.put("emailAddress", "john.doe.ex6am2ple"); // Invalid email address

        CreatePersonCommand employeeCommand = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .params(employeeMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("emailAddress"))
                .andExpect(jsonPath("$.violations[0].message").value("Invalid email address"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddEmployee_InvalidPersonalNumberValidationFailure() throws Exception {
        Map<String, String> employeeMap = new HashMap<>();
        employeeMap.put("firstName", "John");
        employeeMap.put("lastName", "Doe");
        employeeMap.put("personalNumber", "12"); // Invalid personal number
        employeeMap.put("height", "180.5");
        employeeMap.put("weight", "75.0");
        employeeMap.put("emailAddress", "john.doe@ex6am2ple.com");

        CreatePersonCommand employeeCommand = CreatePersonCommand.builder()
                .type("EMPLOYEE")
                .params(employeeMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("personalNumber"))
                .andExpect(jsonPath("$.violations[0].message").value("Personal number must have exactly 11 digits"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddStudent() throws Exception {
        Map<String, String> studentMap = new HashMap<>();
        studentMap.put("firstName", "John");
        studentMap.put("lastName", "Smith");
        studentMap.put("personalNumber", "12345678234");
        studentMap.put("height", "160.0");
        studentMap.put("weight", "55.0");
        studentMap.put("emailAddress", "alice.smith@example.com");
        studentMap.put("universityName", "Example University");
        studentMap.put("academicYear", "2023-09-01");
        studentMap.put("courseName", "Computer Science");
        studentMap.put("scholarship", "1000.0");

        CreatePersonCommand studentCommand = CreatePersonCommand.builder()
                .type("STUDENT")
                .params(studentMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entityType").value("STUDENT"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.personalNumber").value("12345678234"))
                .andExpect(jsonPath("$.height").value(160.0))
                .andExpect(jsonPath("$.weight").value(55.0))
                .andExpect(jsonPath("$.emailAddress").value("alice.smith@example.com"))
                .andExpect(jsonPath("$.universityName").value("Example University"))
                .andExpect(jsonPath("$.academicYear").value("2023-09-01"))
                .andExpect(jsonPath("$.courseName").value("Computer Science"))
                .andExpect(jsonPath("$.scholarship").value(1000.0));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddStudent_InvalidEmailAddressValidationFailure() throws Exception {
        Map<String, String> studentMap = new HashMap<>();
        studentMap.put("firstName", "Alice");
        studentMap.put("lastName", "Smith");
        studentMap.put("personalNumber", "12345678234");
        studentMap.put("height", "160.0");
        studentMap.put("weight", "55.0");
        studentMap.put("emailAddress", "alice.smithexample.com"); // Invalid email address
        studentMap.put("universityName", "Example University");
        studentMap.put("academicYear", "2023-09-01");
        studentMap.put("courseName", "Computer Science");
        studentMap.put("scholarship", "1000.0");

        CreatePersonCommand studentCommand = CreatePersonCommand.builder()
                .type("STUDENT")
                .params(studentMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("emailAddress"))
                .andExpect(jsonPath("$.violations[0].message").value("Invalid email address"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddStudent_InvalidPersonalNumberValidationFailure() throws Exception {
        Map<String, String> studentMap = new HashMap<>();
        studentMap.put("firstName", "Alice");
        studentMap.put("lastName", "Smith");
        studentMap.put("personalNumber", "12"); // Invalid personal number
        studentMap.put("height", "160.0");
        studentMap.put("weight", "55.0");
        studentMap.put("emailAddress", "alice.smith@example.com");
        studentMap.put("universityName", "Example University");
        studentMap.put("academicYear", "2023-09-01");
        studentMap.put("courseName", "Computer Science");
        studentMap.put("scholarship", "1000.0");

        CreatePersonCommand studentCommand = CreatePersonCommand.builder()
                .type("STUDENT")
                .params(studentMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("personalNumber"))
                .andExpect(jsonPath("$.violations[0].message").value("Personal number must have exactly 11 digits"));
    }


    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddPensioner() throws Exception {
        Map<String, String> pensionerMap = new HashMap<>();
        pensionerMap.put("firstName", "Jane");
        pensionerMap.put("lastName", "Smith");
        pensionerMap.put("personalNumber", "98765435101");
        pensionerMap.put("height", "170.0");
        pensionerMap.put("weight", "75.0");
        pensionerMap.put("emailAddress", "jane.smith@eghghxample.com");
        pensionerMap.put("pensionValue", "1100.0");
        pensionerMap.put("yearsWorked", "35");

        CreatePersonCommand pensionerCommand = CreatePersonCommand.builder()
                .type("PENSIONER")
                .params(pensionerMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pensionerCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entityType").value("PENSIONER"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.personalNumber").value("98765435101"))
                .andExpect(jsonPath("$.height").value(170.0))
                .andExpect(jsonPath("$.weight").value(75.0))
                .andExpect(jsonPath("$.emailAddress").value("jane.smith@eghghxample.com"))
                .andExpect(jsonPath("$.pensionValue").value(1100.0))
                .andExpect(jsonPath("$.yearsWorked").value(35));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddPensioner_PersonalNumberValidationFailure() throws Exception {
        Map<String, String> pensionerMap = new HashMap<>();
        pensionerMap.put("firstName", "Jane");
        pensionerMap.put("lastName", "Smith");
        pensionerMap.put("personalNumber", "12"); // Personal number with only 2 digits
        pensionerMap.put("height", "170.0");
        pensionerMap.put("weight", "75.0");
        pensionerMap.put("emailAddress", "jane.smith@eghghxample.com");
        pensionerMap.put("pensionValue", "1100.0");
        pensionerMap.put("yearsWorked", "35");

        CreatePersonCommand pensionerCommand = CreatePersonCommand.builder()
                .type("PENSIONER")
                .params(pensionerMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pensionerCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("personalNumber"))
                .andExpect(jsonPath("$.violations[0].message").value("Personal number must have exactly 11 digits"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddPensioner_InvalidEmail() throws Exception {
        Map<String, String> pensionerMap = new HashMap<>();
        pensionerMap.put("firstName", "Jane");
        pensionerMap.put("lastName", "Smith");
        pensionerMap.put("personalNumber", "98765435101");
        pensionerMap.put("height", "170.0");
        pensionerMap.put("weight", "75.0");
        pensionerMap.put("emailAddress", "jane.smith.example.com"); // Invalid email address
        pensionerMap.put("pensionValue", "1100.0");
        pensionerMap.put("yearsWorked", "35");

        CreatePersonCommand pensionerCommand = CreatePersonCommand.builder()
                .type("PENSIONER")
                .params(pensionerMap)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/data/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pensionerCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[0].field").value("emailAddress"))
                .andExpect(jsonPath("$.violations[0].message").value("Invalid email address"));
    }

}
