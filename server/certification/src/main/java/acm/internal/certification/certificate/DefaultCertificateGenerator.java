package acm.internal.certification.certificate;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
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
public class DefaultCertificateGenerator implements ICertificateGenerator {

    @Override
    public boolean supports(String templateName) {
        return templateName == null || "STANDARD".equalsIgnoreCase(templateName);
    }

    @Override
    public byte[] generate(Certificate certificate) {
        log.info("Generating Default Aesthetic Certificate for: {}", certificate.getRecipientName());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (PdfWriter writer = new PdfWriter(baos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf, PageSize.A4.rotate())) {

                CertificateTemplate template = certificate.getEvent() != null ? certificate.getEvent().getTemplate() : null;
                
                DeviceRgb primaryColor = new DeviceRgb(0, 102, 204);
                String title = (template != null && template.getTitleText() != null) ? template.getTitleText() : "CERTIFICATE OF EXCELLENCE";
                String eventName = certificate.getEvent() != null ? certificate.getEvent().getName() : "General Event";

                // Layout
                PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
                canvas.setStrokeColor(primaryColor).setLineWidth(5).rectangle(20, 20, PageSize.A4.rotate().getWidth() - 40, PageSize.A4.rotate().getHeight() - 40).stroke();
                canvas.setStrokeColor(ColorConstants.GRAY).setLineWidth(1).rectangle(30, 30, PageSize.A4.rotate().getWidth() - 60, PageSize.A4.rotate().getHeight() - 60).stroke();

                Table table = new Table(1).setWidth(PageSize.A4.rotate().getWidth() - 100).setMarginTop(80);
                table.addCell(new Cell().add(new Paragraph(title).setFontSize(48).setBold().setFontColor(primaryColor).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph("PROUDLY PRESENTED TO").setFontSize(16).setMarginTop(30).setCharacterSpacing(2f).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph(certificate.getRecipientName()).setFontSize(42).setItalic().setBold().setMarginTop(10).setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph("In recognition of successful participation in").setFontSize(14).setMarginTop(20).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(new Paragraph(eventName).setFontSize(24).setBold().setMarginTop(5).setFontColor(primaryColor).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

                Table footerTable = new Table(3).setWidth(PageSize.A4.rotate().getWidth() - 200).setMarginTop(60).setMarginLeft(50);
                footerTable.addCell(new Cell().add(new Paragraph(certificate.getIssueDate()).setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
                footerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                footerTable.addCell(new Cell().add(new Paragraph("ACM Coordinator").setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(1)).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

                document.add(table);
                document.add(footerTable);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error in Default PDF Generator", e);
            throw new RuntimeException(e);
        }
    }
}
