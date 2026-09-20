package com.notifan.notifan.delivery;

import com.notifan.notifan.notification.Notification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailingService {

    private final JavaMailSender mailSender;

    public void send(@NonNull Notification notification) throws MailException {
        var msg = new SimpleMailMessage();
        msg.setTo(notification.getRecipientId() + "@notifan.test");
        msg.setSubject("New Notification");
        msg.setText("You have a new " + notification.getEventType() + " notification.");
        mailSender.send(msg);
    }
}
