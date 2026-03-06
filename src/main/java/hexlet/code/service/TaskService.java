package hexlet.code.service;

import hexlet.code.component.TaskSpecification;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskFilter;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public List<TaskDTO> index(TaskFilter filter) {
        var spec = taskSpecification.build(filter);
        return taskRepository.findAll(spec).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public TaskDTO show(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.toDTO(task);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        var task = taskMapper.toEntity(dto);
        taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.updateEntity(dto, task);
        taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    public void destroy(Long id) {
        taskRepository.deleteById(id);
    }
}
