package com.amsidh.mvc.app.service.impl;

import com.amsidh.mvc.app.exception.CustomBusinessException;
import com.amsidh.mvc.app.payload.JobRequest;
import com.amsidh.mvc.app.payload.JobResponse;
import com.amsidh.mvc.app.service.EmailJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;

@Slf4j
public class EmailJobServiceImpl implements EmailJobService {
    @Autowired
    private Scheduler scheduler;

    @Override
    public JobResponse submitJob(JobRequest jobRequest) throws SchedulerException {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(jobRequest.getDateTime(), jobRequest.getTimeZone());
            if (zonedDateTime.isBefore(ZonedDateTime.now())) {
                throw new CustomBusinessException("dateTime must be after current time");
            }

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("email", jobRequest.getEmail());
            jobDataMap.put("subject", jobRequest.getSubject());
            jobDataMap.put("body", jobRequest.getBody());

            JobDetail jobDetail = buildJobDetail("",jobDataMap);
            Trigger jobTrigger = buildTrigger(jobDetail, zonedDateTime);
            scheduler.scheduleJob(jobDetail, jobTrigger);

            return new JobResponse(true, "Email Scheduled Successfully!", jobDetail.getKey().getName(), jobDetail.getKey().getGroup());

    }

    @Override
    public JobDetail buildJobDetail(String jobClass, JobDataMap jobDataMap) {
        return null;
    }

    @Override
    public Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return null;
    }
}
