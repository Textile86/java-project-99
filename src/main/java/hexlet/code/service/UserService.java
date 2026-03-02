package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserPatchDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserUtils userUtils;

    public List<UserDTO> index() {
        List<User> users = userRepository.findAll();
        return  users.stream()
                .map(u -> userMapper.toDTO(u))
                .toList();
    }

    public UserDTO show(Long id) {
        User findedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.toDTO(findedUser);
    }

    public UserDTO create(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPasswordDigest(hashedPassword);

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO update(Long id, UserUpdateDTO dto) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Нет прав на редактирование этого пользователя");
        }
        User findedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.updateEntity(dto, findedUser);
        if (dto.getPassword() != null) {
            findedUser.setPasswordDigest(passwordEncoder.encode(dto.getPassword()));
        }
        User savedUser = userRepository.save(findedUser);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO patch(Long id, UserPatchDTO dto) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Нет прав на редактирование этого пользователя");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.updateEntityFromPatch(dto, user);
        if (dto.getPassword() != null && dto.getPassword().isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
        }
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void destroy(Long id) {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Нет прав на редактирование этого пользователя");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.delete(user);
    }

}
