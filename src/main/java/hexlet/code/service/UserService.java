package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserPatchDTO;
import hexlet.code.dto.UserUpdateDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> index();
    UserDTO show(Long id);
    UserDTO create(UserCreateDTO dto);
    UserDTO update(Long id, UserUpdateDTO dto);
    UserDTO patch(Long id, UserPatchDTO dto);
    void destroy(Long id);
}
