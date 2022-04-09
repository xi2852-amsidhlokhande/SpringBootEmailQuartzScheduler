package com.amsidh.mvc.SpringBootEmailQuartzScheduler.controller;

import com.amsidh.mvc.SpringBootEmailQuartzScheduler.job.EmailJob.EmailJob;
import com.amsidh.mvc.SpringBootEmailQuartzScheduler.payload.EmailRequest;
import com.amsidh.mvc.SpringBootEmailQuartzScheduler.payload.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class EmailSchedulerController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/schedule/email")
    public ResponseEntity<EmailResponse> submitJob(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
            if (zonedDateTime.isBefore(ZonedDateTime.now())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new EmailResponse(false, "dateTime must be after current time"));
            }

            JobDetail emailJobDetail = buildJobDetail(emailRequest);
            Trigger emailTrigger = buildTrigger(emailJobDetail, zonedDateTime);
            scheduler.scheduleJob(emailJobDetail, emailTrigger);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new EmailResponse(true, "Email Scheduled Successfully!", emailJobDetail.getKey().getName(), emailJobDetail.getKey().getGroup()));

        } catch (SchedulerException schedulerException) {
            log.error("Error while scheduling email: ", schedulerException);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailResponse(false, "Error while scheduling email. Please try again later!"));
        }
    }


    @GetMapping("/health/check")
    public ResponseEntity<String> healthStatus(){
        return ResponseEntity.ok("Email Scheduler Service Working");
    }

    private JobDetail buildJobDetail(EmailRequest emailRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs-group")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt) {

        return TriggerBuilder.newTrigger()
                .withIdentity(jobDetail.getKey().getName(), "email-triggers-group")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

}
