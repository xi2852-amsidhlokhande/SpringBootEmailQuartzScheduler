package com.amsidh.mvc.app.service;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.time.ZonedDateTime;

public interface JobService {
    JobDetail buildJobDetail(String jobClass,JobDataMap jobDataMap);
    Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt);
}
