package com.example.prjwebservice.config;

import com.example.prjwebservice.model.entity.Course;
import com.example.prjwebservice.model.entity.EnrollmentStatus;
import com.example.prjwebservice.model.entity.Role;
import com.example.prjwebservice.model.entity.User;
import com.example.prjwebservice.repository.CourseRepository;
import com.example.prjwebservice.repository.EnrollmentStatusRepository;
import com.example.prjwebservice.repository.RoleRepository;
import com.example.prjwebservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentStatusRepository enrollmentStatusRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initEnrollmentStatuses();
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            Role lecturerRole = roleRepository.findByName("LECTURER").orElseThrow();
            Role studentRole = roleRepository.findByName("STUDENT").orElseThrow();

            User admin = userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .email("admin@example.com")
                    .role(adminRole)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .build());

            User lecturer = userRepository.save(User.builder()
                    .username("lecturer")
                    .password(passwordEncoder.encode("lecturer123"))
                    .fullName("Default Lecturer")
                    .email("lecturer@example.com")
                    .role(lecturerRole)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .build());

            User student = userRepository.save(User.builder()
                    .username("student")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Default Student")
                    .email("student@example.com")
                    .role(studentRole)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .build());

            courseRepository.save(Course.builder()
                    .courseCode("PRJ101")
                    .courseName("Project Management and Grading")
                    .description("Default course for system verification")
                    .credits(3)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .lecturer(lecturer)
                    .build());
        }
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ADMIN").build());
            roleRepository.save(Role.builder().name("LECTURER").build());
            roleRepository.save(Role.builder().name("STUDENT").build());
        }
    }

    private void initEnrollmentStatuses() {
        if (enrollmentStatusRepository.count() == 0) {
            enrollmentStatusRepository.save(EnrollmentStatus.builder().name("REGISTERED").build());
            enrollmentStatusRepository.save(EnrollmentStatus.builder().name("DROPPED").build());
        }
    }
}
