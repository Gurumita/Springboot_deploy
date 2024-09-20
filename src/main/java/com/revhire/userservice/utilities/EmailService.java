package com.revhire.userservice.utilities;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    @Autowired
    RandomCredentialsGenerator generator;
    @Autowired
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    String senderEmail;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper messageHelper=new MimeMessageHelper(message,true,"UTF-8");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(body);
        messageHelper.setFrom(senderEmail);

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Password Reset");
        message.setText("Use the following OTP to reset your password: " + otp);
        mailSender.send(message);
    }
}
