package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserPatchDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final UserUtils userUtils;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> index() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO show(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.toDTO(user);
    }

    public UserDTO create(UserCreateDTO dto) {
        var user = userMapper.toEntity(dto);
        user.setPasswordDigest(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    public UserDTO update(Long id, UserUpdateDTO dto) {
        var currentUser = userUtils.getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only edit your own profile");
        }
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.updateEntity(dto, user);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    public UserDTO patch(Long id, UserPatchDTO dto) {
        var currentUser = userUtils.getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only edit your own profile");
        }
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.updateEntityFromPatch(dto, user);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    public void destroy(Long id) {
        var currentUser = userUtils.getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own profile");
        }
        if (taskRepository.existsByAssigneeId(id)) {
            throw new IllegalStateException("Cannot delete user: they are assigned to one or more tasks");
        }
        userRepository.deleteById(id);
    }
}