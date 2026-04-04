package acm.internal.certification.template;

import acm.internal.certification.certificate.Certificate;
import acm.internal.certification.certificate.MassMailService;
import acm.internal.certification.event.Event;
import acm.internal.certification.event.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class TemplateController {

    private final EventRepository eventRepository;
    private final TemplateGeneratorService generatorService;
    private final MassMailService massMailService;
    private static final String UPLOAD_DIR = "templates-upload";

    @GetMapping
    public String index(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadTemplate(@RequestParam("file") MultipartFile file, 
                                 @RequestParam("eventId") Long eventId) throws IOException {
        if (file.isEmpty()) return "redirect:/ui?error=FileEmpty";

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        Path path = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        Files.write(path, file.getBytes());

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            if (event.getTemplate() == null) {
                event.setTemplate(new Template());
            }
            event.getTemplate().setTemplateName("CANVA_IMPORT");
            event.getTemplate().setTemplatePdfPath(path.toAbsolutePath().toString());
            eventRepository.save(event);
            log.info("Uploaded template for event: {} to {}", event.getName(), path);
        }

        return "redirect:/ui?success=Uploaded";
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generate(@RequestParam("recipientName") String recipientName,
                                             @RequestParam("eventId") Long eventId) {
        
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) throw new RuntimeException("Event not found");

        Certificate certificate = Certificate.builder()
                .recipientName(recipientName)
                .event(eventOpt.get())
                .issueDate(LocalDate.now().toString())
                .build();

        byte[] pdf = generatorService.generateCertificatePdf(certificate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdf));
    }

    @PostMapping("/mass-mail")
    public String massMail(@RequestParam("file") MultipartFile file, 
                           @RequestParam("eventId") Long eventId) throws IOException {
        if (file.isEmpty()) return "redirect:/ui?error=CSVEmpty";

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) return "redirect:/ui?error=EventNotFound";

        List<Certificate> recipients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    recipients.add(Certificate.builder()
                            .recipientName(data[0].trim())
                            .recipientEmail(data[1].trim())
                            .event(eventOpt.get())
                            .issueDate(LocalDate.now().toString())
                            .build());
                }
            }
        }

        massMailService.sendCertificates(eventOpt.get(), recipients);
        return "redirect:/ui?success=MassMailStarted&count=" + recipients.size();
    }
}
