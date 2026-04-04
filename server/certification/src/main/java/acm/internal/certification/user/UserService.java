package acm.internal.certification.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public AppUser createUser(AppUser user) {
        log.info("Creating user: {} with email: {}", user.getName(), user.getEmail());
        return userRepository.save(user);
    }

    public AppUser updateUser(Long id, AppUser details) {
        AppUser user = getUserById(id);
        
        user.setName(details.getName());
        user.setEmail(details.getEmail());
        user.setRole(details.getRole());
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        AppUser user = getUserById(id);
        userRepository.delete(user);
        log.info("Deleted user with id: {}", id);
    }
}
