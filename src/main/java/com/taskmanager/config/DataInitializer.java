package com.taskmanager.config;

import com.taskmanager.model.Role;
import com.taskmanager.model.Role.RoleName;
import com.taskmanager.model.User;
import com.taskmanager.model.UserProfile;
import com.taskmanager.repository.RoleRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Creeaza rolurile daca nu exista
        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            roleRepository.save(Role.builder()
                    .name(RoleName.ROLE_USER)
                    .description("Utilizator standard")
                    .build());
            log.info("[INIT] Rol ROLE_USER creat");
        }

        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            roleRepository.save(Role.builder()
                    .name(RoleName.ROLE_ADMIN)
                    .description("Administrator")
                    .build());
            log.info("[INIT] Rol ROLE_ADMIN creat");
        }

        // Creeaza admin default daca nu exista
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@taskmanager.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("System")
                    .active(true)
                    .roles(roles)
                    .build();

            UserProfile profile = UserProfile.builder()
                    .user(admin)
                    .bio("Administrator sistem")
                    .build();
            admin.setProfile(profile);

            userRepository.save(admin);
            log.info("[INIT] User admin creat (username: admin, parola: admin123)");
        }
    }
}
