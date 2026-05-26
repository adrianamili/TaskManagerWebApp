package com.taskmanager.service;

import com.taskmanager.dto.RegisterDto;
import com.taskmanager.exception.DuplicateResourceException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Role;
import com.taskmanager.model.Role.RoleName;
import com.taskmanager.model.User;
import com.taskmanager.model.UserProfile;
import com.taskmanager.repository.RoleRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterDto dto) {
        log.info("[REGISTER] Inregistrare user nou: {}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username-ul '" + dto.getUsername() + "' este deja folosit.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email-ul '" + dto.getEmail() + "' este deja inregistrat.");
        }
        if (!dto.passwordsMatch()) {
            throw new IllegalArgumentException("Parolele nu se potrivesc.");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Rolul USER nu a fost gasit"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .active(true)
                .roles(roles)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();
        user.setProfile(profile);

        User saved = userRepository.save(user);
        log.info("[REGISTER] User inregistrat cu succes: id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User update(Long id, User updated) {
        log.info("[UPDATE] Actualizare user id={}", id);
        User user = findById(id);
        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setEmail(updated.getEmail());
        return userRepository.save(user);
    }

    public void deactivate(Long id) {
        log.info("[DELETE] Dezactivare user id={}", id);
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void promoteToAdmin(Long id) {
        User user = findById(id);
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Rolul ADMIN nu a fost gasit"));
        user.getRoles().add(adminRole);
        userRepository.save(user);
        log.info("[ADMIN] User {} promovat la ADMIN", user.getUsername());
    }
}
