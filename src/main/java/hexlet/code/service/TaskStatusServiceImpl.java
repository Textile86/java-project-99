package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Override
    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::toDTO)
                .toList();
    }

    @Override
    public TaskStatusDTO getById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        return taskStatusMapper.toDTO(taskStatus);
    }

    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var taskStatus = taskStatusMapper.toEntity(dto);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDTO(taskStatus);
    }

    @Override
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        taskStatusMapper.updateEntity(dto, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDTO(taskStatus);
    }

    @Override
    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
