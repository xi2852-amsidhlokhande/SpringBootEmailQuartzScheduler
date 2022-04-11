package com.amsidh.mvc.app.controller;

import com.amsidh.mvc.app.jobs.EmailJob;
import com.amsidh.mvc.app.payload.JobRequest;
import com.amsidh.mvc.app.payload.JobResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
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
public class SchedulerController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/schedule/submitJob")
    public ResponseEntity<JobResponse> submitJob(@Valid @RequestBody JobRequest jobRequest) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(jobRequest.getDateTime(), jobRequest.getTimeZone());
            if (zonedDateTime.isBefore(ZonedDateTime.now())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new JobResponse(false, "dateTime must be after current time"));
            }

            JobDetail jobDetail = buildJobDetail(jobRequest);
            Trigger jobTrigger = buildTrigger(jobDetail, zonedDateTime);
            scheduler.scheduleJob(jobDetail, jobTrigger);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new JobResponse(true, "Email Scheduled Successfully!", jobDetail.getKey().getName(), jobDetail.getKey().getGroup()));

        } catch (SchedulerException schedulerException) {
            log.error("Error while scheduling email: ", schedulerException);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JobResponse(false, "Error while scheduling email. Please try again later!"));
        }
    }


    @GetMapping("/health/check")
    public ResponseEntity<String> healthStatus(){
        return ResponseEntity.ok("Email Scheduler Service Working");
    }

    private JobDetail buildJobDetail(JobRequest jobRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", jobRequest.getEmail());
        jobDataMap.put("subject", jobRequest.getSubject());
        jobDataMap.put("body", jobRequest.getBody());

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
                //.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                .build();
    }

}
