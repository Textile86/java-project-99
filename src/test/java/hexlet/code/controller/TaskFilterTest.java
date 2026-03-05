package hexlet.code.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private TaskStatus statusDraft;
    private TaskStatus statusReview;
    private Label bugLabel;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordDigest(passwordEncoder.encode("password"));
        userRepository.save(testUser);

        statusDraft = new TaskStatus();
        statusDraft.setName("Draft");
        statusDraft.setSlug("draft");
        taskStatusRepository.save(statusDraft);

        statusReview = new TaskStatus();
        statusReview.setName("ToReview");
        statusReview.setSlug("to_review");
        taskStatusRepository.save(statusReview);

        bugLabel = new Label();
        bugLabel.setName("bug");
        labelRepository.save(bugLabel);

        task1 = new Task();
        task1.setName("Fix login bug");
        task1.setTaskStatus(statusDraft);
        task1.setAssignee(testUser);
        task1.setLabels(Set.of(bugLabel));
        taskRepository.save(task1);

        task2 = new Task();
        task2.setName("Create new feature");
        task2.setTaskStatus(statusReview);
        taskRepository.save(task2);
    }

    @Test
    void testIndexWithoutFilter() throws Exception {
        mockMvc.perform(get("/api/tasks").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testFilterByTitleCont() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=login").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Fix login bug"));
    }

    @Test
    void testFilterByTitleContCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=LOGIN").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testFilterByAssigneeId() throws Exception {
        mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId()).with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Fix login bug"));
    }

    @Test
    void testFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/tasks?status=draft").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("draft"));
    }

    @Test
    void testFilterByLabelId() throws Exception {
        mockMvc.perform(get("/api/tasks?labelId=" + bugLabel.getId()).with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Fix login bug"));
    }

    @Test
    void testFilterCombined() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=fix&status=draft&assigneeId=" + testUser.getId())
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Fix login bug"));
    }

    @Test
    void testFilterNoMatch() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=nonexistent").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
