package com.amsidh.mvc.app.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
@JsonInclude(Include.NON_NULL)
@Data
public class JobResponse {

    private Boolean status;
    private String message;

    private String jobId;
    private String jobGroup;

    public JobResponse(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public JobResponse(Boolean status, String message, String jobId, String jobGroup) {
        this.status = status;
        this.message = message;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
    }
}
