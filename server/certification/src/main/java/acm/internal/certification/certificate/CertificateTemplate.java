package acm.internal.certification.certificate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String templateName;
    private String backgroundColor; // Hex value
    private String titleText;
    private Integer titleFontSize;
    private String logoUrl;
    private String templatePdfPath;
}
