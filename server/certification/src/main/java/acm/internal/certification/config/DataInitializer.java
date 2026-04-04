package acm.internal.certification.config;

import acm.internal.certification.certificate.Certificate;
import acm.internal.certification.certificate.CertificateRepository;
import acm.internal.certification.template.Template;
import acm.internal.certification.event.Event;
import acm.internal.certification.event.EventRepository;
import acm.internal.certification.user.AppUser;
import acm.internal.certification.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CertificateRepository certificateRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Seed User
            String testEmail = "test@acm.org";
            if (userRepository.findByEmail(testEmail).isEmpty()) {
                AppUser testUser = new AppUser();
                testUser.setName("ACM Admin");
                testUser.setEmail(testEmail);
                testUser.setPassword(passwordEncoder.encode("testpass123"));
                testUser.setRole("ADMIN");
                userRepository.save(testUser);
                log.info("Initialized test user: {} with password: testpass123", testEmail);
            }
            // Seed Event
            if (eventRepository.findAll().isEmpty()) {
                Template defaultTemplate = new Template();
                defaultTemplate.setTemplateName("Default Template");
                defaultTemplate.setTemplatePdfPath("templates/default.pdf");

                Event testEvent = new Event();
                testEvent.setName("ACM Hackathon 2024");
                testEvent.setEventDate("2024-05-15");
                testEvent.setTemplate(defaultTemplate);
                
                eventRepository.save(testEvent);
                log.info("Initialized test event: ACM Hackathon 2024 with default template");
            }

        };
    }
}
