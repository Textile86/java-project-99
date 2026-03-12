package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();

        testLabel = new Label();
        testLabel.setName("bug");
        labelRepository.save(testLabel);
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/labels")
                        .with(user("admin@example.com")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<LabelDTO> actual = objectMapper.readValue(body, new TypeReference<>() { });

        var expected = labelRepository.findAll();

        assertThat(actual).hasSize(expected.size());
        assertThat(actual.stream().map(LabelDTO::getName).toList())
                .containsExactlyInAnyOrderElementsOf(
                        expected.stream().map(Label::getName).toList()
                );
    }

    @Test
    void testShow() throws Exception {
        mockMvc.perform(get("/api/labels/" + testLabel.getId())
                        .with(user("admin@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bug"));
    }

    @Test
    void testCreate() throws Exception {
        var data = Map.of("name", "feature");

        mockMvc.perform(post("/api/labels")
                        .with(user("admin@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("feature"))
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(labelRepository.findByName("feature")).isPresent();
    }

    @Test
    void testCreateUnauthorized() throws Exception {
        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "test"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateTooShortName() throws Exception {
        mockMvc.perform(post("/api/labels")
                        .with(user("admin@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "ab"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        var data = Map.of("name", "Bug Fixed");

        mockMvc.perform(put("/api/labels/" + testLabel.getId())
                        .with(user("admin@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bug Fixed"));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                        .with(user("admin@example.com")))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }

    @Test
    void testDeleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId()))
                .andExpect(status().isUnauthorized());
    }

}
