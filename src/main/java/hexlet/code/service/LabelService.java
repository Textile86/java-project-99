package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private final LabelMapper labelMapper;

    public List<LabelDTO> index() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDTO)
                .toList();
    }

    public LabelDTO show(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.toDTO(label);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        var label = labelMapper.toEntity(dto);
        labelRepository.save(label);
        return labelMapper.toDTO(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO dto) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.updateEntity(dto, label);
        labelRepository.save(label);
        return labelMapper.toDTO(label);
    }

    public void destroy(Long id) {
        if (taskRepository.existsByLabelsId(id)) {
            throw new IllegalStateException("Cannot delete label: it is used by one or more tasks");
        }
        labelRepository.deleteById(id);
    }
}
