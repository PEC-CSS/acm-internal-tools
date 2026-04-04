package acm.internal.certification.certificate;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class HackathonCertificateGenerator implements ICertificateGenerator {

    @Override
    public boolean supports(String templateName) {
        return "HACKATHON".equals(templateName);
    }

    @Override
    public byte[] generate(Certificate certificate) {
        log.info("Generating HACKATHON Style Certificate for: {}", certificate.getRecipientName());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (PdfWriter writer = new PdfWriter(baos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf, PageSize.A4.rotate())) {

                DeviceRgb darkRed = new DeviceRgb(153, 0, 0); // Hackathon-ish dark red
                String eventName = certificate.getEvent() != null ? certificate.getEvent().getName() : "General Event";

                Table table = new Table(1).setWidth(PageSize.A4.rotate().getWidth() - 100).setMarginTop(120);
                table.addCell(new Cell().add(new Paragraph("CODE. CREATE. CONQUER.")
                        .setFontSize(28).setBold().setFontColor(darkRed).setItalic().setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
                
                table.addCell(new Cell().add(new Paragraph(eventName)
                        .setFontSize(36).setBold().setMarginTop(20).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

                table.addCell(new Cell().add(new Paragraph("This is to certify that " + certificate.getRecipientName())
                        .setFontSize(22).setMarginTop(30).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

                table.addCell(new Cell().add(new Paragraph("has shown exceptional technical skill and innovation during the marathon event.")
                        .setFontSize(16).setItalic().setMarginTop(20).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

                document.add(table);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error in Hackathon PDF Generator", e);
            throw new RuntimeException(e);
        }
    }
}
