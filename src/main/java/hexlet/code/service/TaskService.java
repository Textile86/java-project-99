package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskFilter;
import hexlet.code.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> index(TaskFilter filter);
    TaskDTO show(Long id);
    TaskDTO create(TaskCreateDTO dto);
    TaskDTO update(Long id, TaskUpdateDTO dto);
    void destroy(Long id);
}
