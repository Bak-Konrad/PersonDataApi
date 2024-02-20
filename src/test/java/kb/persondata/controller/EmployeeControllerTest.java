package kb.persondata.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import kb.persondata.employee.model.Employee;
import kb.persondata.mapper.GeneralMapper;
import kb.persondata.person.PersonRepository;
import kb.persondata.position.PositionRepository;
import kb.persondata.position.model.command.CreatePositionCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PositionRepository positionRepository;

    @AfterEach
    void tearDown() {
        positionRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void testAddPositionForEmployee_RoleAdmin() throws Exception {
        Employee employee = Employee.builder()
                .entityType("EMPLOYEE")
                .Id(1L)
                .firstName("John")
                .lastName("Doe")
                .personalNumber("12346678901")
                .height(180.5)
                .weight(75.0)
                .emailAddress("john.doe@ex6am2ple.com")
                .build();
        Employee employee1 = personRepository.saveAndFlush(employee);
        Long employeeId = employee1.getId();
        CreatePositionCommand positionCommand = new CreatePositionCommand();
        positionCommand.setPositionName("Software Engineer");
        positionCommand.setFromDate(LocalDate.of(2022, 1, 1));
        positionCommand.setToDate(LocalDate.of(2023, 12, 31));
        positionCommand.setSalary(new BigDecimal("6000.00"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/data/employees/{employeeId}/positions", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.positionName").value("Software Engineer"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_EMPLOYEE"})
    void testAddPositionForEmployee_RoleEmployee() throws Exception {
        Employee employee = Employee.builder()
                .entityType("EMPLOYEE")
                .Id(1L)
                .firstName("John")
                .lastName("Doe")
                .personalNumber("12346678901")
                .height(180.5)
                .weight(75.0)
                .emailAddress("john.doe@ex6am2ple.com")
                .build();
        personRepository.saveAndFlush(employee);
        Long employeeId = 1L;
        CreatePositionCommand positionCommand = new CreatePositionCommand();
        positionCommand.setPositionName("Software Engineer");
        positionCommand.setFromDate(LocalDate.of(2022, 1, 1));
        positionCommand.setToDate(LocalDate.of(2023, 12, 31));
        positionCommand.setSalary(new BigDecimal("6000.00"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/data/employees/{employeeId}/positions", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.positionName").value("Software Engineer"));
    }

    @Test
    @WithAnonymousUser
    void testAddPositionForEmployee_UnauthorizedUser() throws Exception {

        long employeeId = 1L;
        CreatePositionCommand positionCommand = new CreatePositionCommand();
        positionCommand.setPositionName("Software Engineer");
        positionCommand.setSalary(new BigDecimal("75000.00"));
        positionCommand.setFromDate(LocalDate.parse("2023-10-20"));
        positionCommand.setToDate(LocalDate.parse("2023-11-19"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/data/employees/{employeeId}/positions", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionCommand)))
                .andExpect(status().isUnauthorized());
    }

}
