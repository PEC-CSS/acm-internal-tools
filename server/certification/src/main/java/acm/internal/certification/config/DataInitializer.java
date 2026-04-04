package acm.internal.certification.config;

import acm.internal.certification.certificate.Certificate;
import acm.internal.certification.certificate.CertificateRepository;
import acm.internal.certification.certificate.CertificateTemplate;
import acm.internal.certification.certificate.Event;
import acm.internal.certification.certificate.EventRepository;
import acm.internal.certification.user.AppUser;
import acm.internal.certification.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Seed User
            String testEmail = "test@acm.org";
            if (userRepository.findByEmail(testEmail).isEmpty()) {
                AppUser testUser = AppUser.builder()
                        .name("ACM Admin")
                        .email(testEmail)
                        .password(passwordEncoder.encode("testpass123"))
                        .role("ADMIN")
                        .build();
                userRepository.save(testUser);
                log.info("Initialized test user: {} with password: testpass123", testEmail);
            }

            // Seed Event with Template
            if (eventRepository.findAll().isEmpty()) {
                CertificateTemplate template = CertificateTemplate.builder()
                        .templateName("STANDARD")
                        .titleText("CERTIFICATE OF EXCELLENCE")
                        .titleFontSize(28)
                        .backgroundColor("#FFFFFF")
                        .build();

                Event event = Event.builder()
                        .name("ACM Spring Hackathon 2026")
                        .eventDate(LocalDate.now().toString())
                        .template(template)
                        .build();
                
                Event savedEvent = eventRepository.save(event);
                log.info("Initialized test event: {}", savedEvent.getName());

                // Seed Certificate linked to Event
                if (certificateRepository.findAll().isEmpty()) {
                    Certificate testCert = Certificate.builder()
                            .recipientName("John Doe")
                            .recipientEmail("john.doe@example.com")
                            .event(savedEvent)
                            .issueDate(LocalDate.now().toString())
                            .certificateUrl("http://localhost:8080/api/certificates/1/download")
                            .build();
                    certificateRepository.save(testCert);
                    log.info("Initialized test certificate for: {} in event: {}", 
                            testCert.getRecipientName(), savedEvent.getName());
                }
            }
        };
    }
}
