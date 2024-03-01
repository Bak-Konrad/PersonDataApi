package kb.persondata.controller;

import kb.persondata.csvimport.importstatus.CsvImportStatusRepository;
import kb.persondata.csvimport.importstatus.model.ImportStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CsvImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CsvImportStatusRepository importStatusRepository;

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testAddPersonsFromFile_ForAdmin() throws Exception {
        Path filePath = Paths.get("src/main/resources/csvFileForTesting.txt");
        byte[] fileContent = Files.readAllBytes(filePath);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "csvDataForTesting.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileContent
        );
        String statusCommand = "ImportId";

        mockMvc.perform(multipart("/api/v1/data/csvfiles")
                        .file(file)
                        .param("statusCommand", statusCommand))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(jsonPath("$.status").value("In progress"));
    }

    @Test
    @WithMockUser(authorities = {"IMPORTER"})
    void testAddPersonsFromFile_ForImporter() throws Exception {
        Path filePath = Paths.get("src/main/resources/csvFileForTesting.txt");
        byte[] fileContent = Files.readAllBytes(filePath);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "csvDataForTesting.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileContent
        );
        String statusCommand = "ImportId";

        mockMvc.perform(multipart("/api/v1/data/csvfiles")
                        .file(file)
                        .param("statusCommand", statusCommand))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(jsonPath("$.status").value("In progress"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN", "IMPORTER"})
    void testGetImportStatus() throws Exception {
        Long importId = 1L;
        ImportStatus importStatus = ImportStatus.builder()
                .creationDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .status("Done")
                .statusId(importId)
                .build();
        importStatusRepository.saveAndFlush(importStatus);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/data/csvfiles/{importId}", importId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(importId));
    }
}

