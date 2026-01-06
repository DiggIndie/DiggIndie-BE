package ceos.diggindie.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code, String purpose) {
        String subject;
        String content;

        if ("signup".equals(purpose)) {
            subject = "[Diggindie] 회원가입 인증 코드";
            content = buildSignupEmailContent(code);
        } else {
            subject = "[Diggindie] 비밀번호 재설정 인증 코드";
            content = buildPasswordResetEmailContent(code);
        }

        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    private String buildSignupEmailContent(String code) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #333;">Diggindie 회원가입 인증</h2>
                <p>아래 인증 코드를 입력하여 회원가입을 완료해주세요.</p>
                <div style="background-color: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0;">
                    <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #007bff;">%s</span>
                </div>
                <p style="color: #666;">이 코드는 5분간 유효합니다.</p>
                <p style="color: #999; font-size: 12px;">본인이 요청하지 않은 경우 이 이메일을 무시해주세요.</p>
            </body>
            </html>
            """.formatted(code);
    }

    private String buildPasswordResetEmailContent(String code) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #333;">Diggindie 비밀번호 재설정</h2>
                <p>아래 인증 코드를 입력하여 비밀번호를 재설정해주세요.</p>
                <div style="background-color: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0;">
                    <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #dc3545;">%s</span>
                </div>
                <p style="color: #666;">이 코드는 5분간 유효합니다.</p>
                <p style="color: #999; font-size: 12px;">본인이 요청하지 않은 경우 이 이메일을 무시해주세요.</p>
            </body>
            </html>
            """.formatted(code);
    }
}