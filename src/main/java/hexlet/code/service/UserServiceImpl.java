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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final UserUtils userUtils;
    private final PasswordEncoder passwordEncoder;

    private static final String ACTION_USER = "User with id ";
    private static final String ACTION_NOT_FOUND = " not found";

    @Override
    public List<UserDTO> index() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public UserDTO show(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO create(UserCreateDTO dto) {
        var user = userMapper.toEntity(dto);
        user.setPasswordDigest(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO update(Long id, UserUpdateDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));
        userMapper.updateEntity(dto, user);
        if (dto.getPassword() != null && dto.getPassword().isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
        }
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO patch(Long id, UserPatchDTO dto) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));
        userMapper.updateEntityFromPatch(dto, user);
        if (dto.getPassword() != null && dto.getPassword().isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
        }
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    public void destroy(Long id) {
        userRepository.deleteById(id);
    }
}
