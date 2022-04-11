package com.amsidh.mvc.app.jobs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class EmailJob extends QuartzJobBean {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Sending Email Job Started!!");
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String recipientEmail = jobDataMap.getString("email");
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");

        sendEmail(mailProperties.getUsername(), recipientEmail, subject, body);

    }

    @SneakyThrows(value = MessagingException.class)
    private void sendEmail(String fromEmail, String toEmail, String subject, String body) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);

        mailSender.send(mimeMessage);
        log.info("Email Sent Successfully!!!");
    }
}
