package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
        User user = createTestUser();

        MvcResult result = mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(user.getEmail()),
                v -> v.node("firstName").isEqualTo(user.getFirstName())
        );
    }

    @Test
    void testProtectedEndpointUnauthorized() throws Exception {
        User user = createTestUser();
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "John");
        data.put("lastName", "Smith");
        data.put("email", "john@example.com");
        data.put("password", "password123");

        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        User user = userRepository.findAll().get(0);
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testPatch() throws Exception {
        User user = createTestUser();
        String oldLastName = user.getLastName();

        var data = new HashMap<>();
        data.put("firstName", "NewFirstName");
        data.put("email", "newemail@example.com");

        mockMvc.perform(patch("/api/users/" + user.getId())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).get();
        assertThat(updatedUser.getFirstName()).isEqualTo("NewFirstName");
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUser.getLastName()).isEqualTo(oldLastName);
    }

    @Test
    void testPatchForbidden() throws Exception {
        User user = createTestUser();
        User anotherUser = createTestUser();

        var data = new HashMap<>();
        data.put("firstName", "Hacker");

        mockMvc.perform(patch("/api/users/" + user.getId())
                        .with(user(anotherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDestroy() throws Exception {
        User user = createTestUser();

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .with(user(user)))  // <-- и здесь
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    @Test
    void testDestroyForbidden() throws Exception {
        User user = createTestUser();
        User anotherUser = createTestUser();

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .with(user(anotherUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDestroyForbiddenWhenAssignedToTask() throws Exception {
        User user = createTestUser();

        TaskStatus status = taskStatusRepository.findAll().get(0);

        Task task = new Task();
        task.setName("Blocking task");
        task.setTaskStatus(status);
        task.setAssignee(user);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .with(user(user)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdate() throws Exception {
        User user = createTestUser();

        var data = new HashMap<>();
        data.put("firstName", "Updated");
        data.put("lastName", "Name");
        data.put("email", "updated@example.com");
        data.put("password", "newpassword");

        mockMvc.perform(patch("/api/users/" + user.getId())
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).get();
        assertThat(updated.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void testUpdateForbidden() throws Exception {
        User user = createTestUser();
        User anotherUser = createTestUser();
        String originalFirstName = user.getFirstName();

        var data = new HashMap<>();
        data.put("firstName", "Hacker");
        data.put("lastName", "Attack");
        data.put("email", "hacked@example.com");
        data.put("password", "hackedpass");

        mockMvc.perform(patch("/api/users/" + user.getId())
                        .with(user(anotherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isForbidden());

        User notUpdated = userRepository.findById(user.getId()).get();
        assertThat(notUpdated.getFirstName()).isEqualTo(originalFirstName);
    }

    private User createTestUser() {
        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .create();

        user.setPasswordDigest("$2a$10$someHashedPassword");
        return userRepository.save(user);
    }
}
