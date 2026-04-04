package acm.internal.certification.config;

import acm.internal.certification.certificate.Certificate;
import acm.internal.certification.certificate.CertificateGeneratorService;
import acm.internal.certification.certificate.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RootController {

    private final CertificateRepository certificateRepository;
    private final CertificateGeneratorService certificateGeneratorService;

    @GetMapping("/")
    public ResponseEntity<byte[]> getRootCertificate() {
        return certificateRepository.findAll().stream()
                .findFirst()
                .map(cert -> {
                    byte[] pdfBytes = certificateGeneratorService.generateCertificatePdf(cert);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificate.pdf")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdfBytes);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
