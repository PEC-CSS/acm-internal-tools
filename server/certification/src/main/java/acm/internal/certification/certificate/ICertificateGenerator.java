package acm.internal.certification.certificate;

public interface ICertificateGenerator {
    byte[] generate(Certificate certificate);
    boolean supports(String templateName);
}
