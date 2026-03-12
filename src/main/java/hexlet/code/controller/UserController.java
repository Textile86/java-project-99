package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserPatchDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String ONLY_OWNER_BY_ID =
            "@userUtils.getCurrentUser().getId() == #id";

    @GetMapping
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> userDTOs = userService.index();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(userDTOs.size()))
                .body(userDTOs);
    }

    @GetMapping("/{id}")
    public UserDTO show(@PathVariable Long id) {
        return userService.show(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return userService.update(id, dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public UserDTO patch(@PathVariable Long id, @Valid @RequestBody UserPatchDTO dto) {
        return userService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userService.destroy(id);
    }


}
