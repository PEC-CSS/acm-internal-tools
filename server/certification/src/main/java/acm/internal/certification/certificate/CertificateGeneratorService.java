package acm.internal.certification.certificate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateGeneratorService {

    private final List<ICertificateGenerator> generators;

    public byte[] generateCertificatePdf(Certificate certificate) {
        String templateName = null;
        if (certificate.getEvent() != null && certificate.getEvent().getTemplate() != null) {
            templateName = certificate.getEvent().getTemplate().getTemplateName();
        }

        log.info("Dispatching PDF generation request for template: {}", templateName);

        // Map templateName to correct generator
        String finalTemplateName = templateName;
        return generators.stream()
                .filter(g -> g.supports(finalTemplateName))
                .findFirst()
                .map(g -> g.generate(certificate))
                .orElseThrow(() -> new RuntimeException("No generator found for template: " + finalTemplateName));
    }
}
