package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LabelRepository labelRepository;

    private Task testTask;
    private TaskStatus testStatus;
    private User testUser;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        testStatus = new TaskStatus();
        testStatus.setName("Draft");
        testStatus.setSlug("draft");
        taskStatusRepository.save(testStatus);

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordDigest(passwordEncoder.encode("password"));
        userRepository.save(testUser);

        testTask = new Task();
        testTask.setName("Test task");
        testTask.setTaskStatus(testStatus);
        testTask.setAssignee(testUser);
        taskRepository.save(testTask);
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks")
                        .with(user(testUser)))
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
    void testIndexUnauthorized() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testShow() throws Exception {
        mockMvc.perform(get("/api/tasks/" + testTask.getId())
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test task"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assignee_id").value(testUser.getId()));
    }

    @Test
    void testCreate() throws Exception {
        var data = Map.of(
                "title", "New task",
                "status", "draft",
                "assignee_id", testUser.getId()
        );

        mockMvc.perform(post("/api/tasks")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New task"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(taskRepository.count()).isEqualTo(2);
    }

    @Test
    void testCreateUnauthorized() throws Exception {
        var data = Map.of("title", "New task", "status", "draft");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateInvalidData() throws Exception {
        // Без обязательного поля status
        var data = Map.of("title", "No status task");

        mockMvc.perform(post("/api/tasks")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        var data = Map.of("title", "Updated title", "content", "Updated content");

        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.status").value("draft"));

        var updated = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated title");
    }

    @Test
    void testUpdateUnauthorized() throws Exception {
        var data = Map.of("title", "Should fail");

        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId())
                        .with(user(testUser)))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }

    @Test
    void testDeleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateWithLabels() throws Exception {
        var label = new Label();
        label.setName("test");
        labelRepository.save(label);

        var data = new HashMap<>();
        data.put("title", "Task with labels");
        data.put("status", "draft");
        data.put("taskLabelIds", Set.of(label.getId()));

        mockMvc.perform(post("/api/tasks")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskLabelIds").isArray());
    }

    @Test
    void testCreateWithInvalidStatus() throws Exception {
        var data = Map.of(
                "title", "Bad status task",
                "status", "nonexistent-slug"
        );

        mockMvc.perform(post("/api/tasks")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateWithInvalidAssignee() throws Exception {
        var data = new HashMap<>();
        data.put("title", "Task bad assignee");
        data.put("status", "draft");
        data.put("assignee_id", 999999L);

        mockMvc.perform(post("/api/tasks")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithNewStatus() throws Exception {
        var newStatus = new TaskStatus();
        newStatus.setName("In Progress");
        newStatus.setSlug("in-progress");
        taskStatusRepository.save(newStatus);

        var data = Map.of("status", "in-progress");

        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("in-progress"));
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/999999")
                        .with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithAssignee() throws Exception {
        var data = new HashMap<>();
        data.put("assignee_id", testUser.getId());

        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignee_id").value(testUser.getId()));
    }

    @Test
    void testUpdateWithLabels() throws Exception {
        var label = new Label();
        label.setName("feature-label");
        labelRepository.save(label);

        var data = new HashMap<>();
        data.put("taskLabelIds", Set.of(label.getId()));

        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskLabelIds").isArray());
    }

}
