package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskFilter;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.service.TaskServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskServiceImpl taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> index(TaskFilter filter) {
        var list = taskService.index(filter);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(list.size()))
                .body(list);
    }

    @GetMapping("/{id}")
    public TaskDTO show(@PathVariable Long id) {
        return taskService.show(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO dto) {
        return taskService.create(dto);
    }

    @PutMapping("/{id}")
    public TaskDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO dto) {
        return taskService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.destroy(id);
    }

}
