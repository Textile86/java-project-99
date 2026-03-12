package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private TaskStatus testStatus;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        testStatus = new TaskStatus();
        testStatus.setName("Draft");
        testStatus.setSlug("draft");
        taskStatusRepository.save(testStatus);
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks")
                        .with(user("admin@example.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<TaskDTO> actual = objectMapper.readValue(
                response.getContentAsString(), new TypeReference<>() { });

        var expected = taskRepository.findAll();

        assertThat(actual).hasSize(expected.size());
        assertThat(actual.stream().map(TaskDTO::getName).toList())
                .containsExactlyInAnyOrderElementsOf(
                        expected.stream().map(Task::getName).toList()
                );
    }

    @Test
    void testShow() throws Exception {
        mockMvc.perform(get("/api/task_statuses/" + testStatus.getId())
                        .with(user("admin@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Draft"))
                .andExpect(jsonPath("$.slug").value("draft"));
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/task_statuses/99999")
                        .with(user("admin@example.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var data = Map.of("name", "ToReview", "slug", "to_review");

        mockMvc.perform(post("/api/task_statuses")
                        .with(user("admin@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ToReview"))
                .andExpect(jsonPath("$.slug").value("to_review"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(taskStatusRepository.findBySlug("to_review")).isPresent();
    }

    @Test
    void testCreateUnauthorized() throws Exception {
        var data = Map.of("name", "NewStatus", "slug", "new_status");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateInvalidData() throws Exception {
        var data = Map.of("name", "", "slug", "");

        mockMvc.perform(post("/api/task_statuses")
                        .with(user("admin@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        var data = Map.of("name", "Updated");

        mockMvc.perform(put("/api/task_statuses/" + testStatus.getId())
                        .with(user("admin@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.slug").value("draft")); // slug не изменился

        var updated = taskStatusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void testUpdateUnauthorized() throws Exception {
        var data = Map.of("name", "ShouldFail");

        mockMvc.perform(put("/api/task_statuses/" + testStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId())
                        .with(user("admin@example.com")))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findById(testStatus.getId())).isEmpty();
    }

    @Test
    void testDeleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId()))
                .andExpect(status().isUnauthorized());
    }
}
