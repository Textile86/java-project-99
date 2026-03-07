package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final LabelRepository labelRepository;

    @Value("${app.admin.password:qwerty}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedTaskStatuses();
        seedLabels();
    }

    private void seedAdmin() {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            var user = new User();
            user.setEmail("admin@example.com");
            user.setPasswordDigest(passwordEncoder.encode(adminPassword));
            userRepository.save(user);
        }
    }

    private void seedTaskStatuses() {
        var defaultStatuses = Map.of(
                "draft", "Draft",
                "to_review", "ToReview",
                "to_be_fixed", "ToBeFixed",
                "to_publish", "ToPublish",
                "published", "Published"
        );

        defaultStatuses.forEach((slug, name) -> {
            if (taskStatusRepository.findBySlug(slug).isEmpty()) {
                var status = new TaskStatus();
                status.setSlug(slug);
                status.setName(name);
                taskStatusRepository.save(status);
            }
        });
    }

    private void seedLabels() {
        var defaultLabels = List.of("duplicate", "bug", "documentation", "enhancement", "invalid", "question");
        defaultLabels.forEach(name -> {
            if (labelRepository.findByName(name).isEmpty()) {
                var label = new Label();
                label.setName(name);
                labelRepository.save(label);
            }
        });
    }
}
