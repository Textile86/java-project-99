package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelDTO> index();
    LabelDTO show(Long id);
    LabelDTO create(LabelCreateDTO dto);
    LabelDTO update(Long id, LabelUpdateDTO dto);
    void destroy(Long id);
}
