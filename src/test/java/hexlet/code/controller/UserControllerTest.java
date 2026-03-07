package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
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

    @BeforeEach
    public void setUp() {
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
