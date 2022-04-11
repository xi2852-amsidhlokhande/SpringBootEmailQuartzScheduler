package com.amsidh.mvc.app.service;

import com.amsidh.mvc.app.payload.JobRequest;
import com.amsidh.mvc.app.payload.JobResponse;
import org.quartz.SchedulerException;

public interface EmailJobService extends JobService{
    JobResponse submitJob(JobRequest jobRequest) throws SchedulerException;
}
