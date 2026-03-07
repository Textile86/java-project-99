package hexlet.code.service;

import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void createUser(UserDetails user) {
        // Unused method
    }

    @Override
    public void updateUser(UserDetails user) {
        // Unused method
    }

    @Override
    public void deleteUser(String username) {
        // Unused method
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // Unused method
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }
}
