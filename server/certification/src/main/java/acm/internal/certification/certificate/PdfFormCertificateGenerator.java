package acm.internal.certification.certificate;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Slf4j
@Service
public class PdfFormCertificateGenerator implements ICertificateGenerator {

    @Override
    public boolean supports(String templateName) {
        return "CANVA_IMPORT".equalsIgnoreCase(templateName);
    }

    @Override
    public byte[] generate(Certificate certificate) {
        CertificateTemplate template = certificate.getEvent().getTemplate();
        String path = template.getTemplatePdfPath();

        log.info("Generating certificate by filling pre-designed PDF form: {}", path);

        if (path == null || !new File(path).exists()) {
            log.error("Template PDF file not found at path: {}", path);
            throw new RuntimeException("Template PDF file missing. Please upload the Canva PDF to: " + path);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Use reader to load template and writer to save the result
            try (PdfReader reader = new PdfReader(path);
                 PdfWriter writer = new PdfWriter(baos);
                 PdfDocument pdf = new PdfDocument(reader, writer)) {

                PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
                
                // Dynamically populate fields if they exist in the PDF
                if (form.getField("recipient_name") != null) {
                    form.getField("recipient_name").setValue(certificate.getRecipientName());
                }
                if (form.getField("event_name") != null) {
                    form.getField("event_name").setValue(certificate.getEvent().getName());
                }
                if (form.getField("issue_date") != null) {
                    form.getField("issue_date").setValue(certificate.getIssueDate());
                }

                form.flattenFields(); // Merge fields into the page content
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate certificate from PDF form", e);
            throw new RuntimeException("Internal PDF processing error", e);
        }
    }
}
