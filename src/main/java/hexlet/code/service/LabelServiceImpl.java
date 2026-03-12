package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public List<LabelDTO> index() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDTO)
                .toList();
    }

    @Override
    public LabelDTO show(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.toDTO(label);
    }

    @Override
    public LabelDTO create(LabelCreateDTO dto) {
        var label = labelMapper.toEntity(dto);
        labelRepository.save(label);
        return labelMapper.toDTO(label);
    }

    @Override
    public LabelDTO update(Long id, LabelUpdateDTO dto) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.updateEntity(dto, label);
        labelRepository.save(label);
        return labelMapper.toDTO(label);
    }

    @Override
    public void destroy(Long id) {
        labelRepository.deleteById(id);
    }
}
