package acm.internal.certification.certificate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle bulk certificate generation and email distribution in a background job.
 * Uses batch mailing to optimize SMTP connection usage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MassMailService {

    private final JavaMailSender mailSender;
    private final CertificateGeneratorService generatorService;

    /**
     * Entry point for the asynchronous mailing job.
     */
    @Async
    public void sendCertificates(Event event, List<RecipientData> recipients) {
        log.info("Starting batch mass mail job for event: {} with {} recipients", event.getName(), recipients.size());

        List<MimeMessage> messages = new ArrayList<>();

        for (RecipientData data : recipients) {
            try {
                // Construct recipient certificate metadata
                Certificate certificate = Certificate.builder()
                        .recipientName(data.name())
                        .recipientEmail(data.email())
                        .event(event)
                        .issueDate(LocalDate.now().toString())
                        .build();

                // 1. Generate the personalized PDF
                byte[] pdfBytes = generatorService.generateCertificatePdf(certificate);

                // 2. Wrap it in a MimeMessage
                MimeMessage message = prepareMessage(data.email(), event.getName(), data.name(), pdfBytes);
                messages.add(message);
                
                log.info("Successfully prepared certificate for: {}", data.email());
            } catch (Exception e) {
                log.error("Failed to prepare certificate for recipient: {}", data.email(), e);
            }
        }

        // 3. Dispatch all messages in a single SMTP batch connection
        if (!messages.isEmpty()) {
            try {
                mailSender.send(messages.toArray(new MimeMessage[0]));
                log.info("Batch dispatch successful! Sent {} certificates for event: {}", messages.size(), event.getName());
            } catch (Exception e) {
                log.error("SMTP Batch dispatch failed. Please check your mail configurations.", e);
            }
        } else {
            log.warn("Mass mail job ended without sending any emails. Check previous error logs.");
        }
    }

    /**
     * Prepares an individual email with attachment.
     */
    private MimeMessage prepareMessage(String to, String eventName, String recipientName, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Your Certificate for " + eventName);
        helper.setText("Dear " + recipientName + ",\n\nCongratulations! Please find your participation certificate for " + eventName + " attached.\n\nBest regards,\nACM Internal Certification Team");

        String filename = "Certificate_" + eventName.replace(" ", "_") + ".pdf";
        helper.addAttachment(filename, new ByteArrayResource(pdfBytes));

        return message;
    }
}
