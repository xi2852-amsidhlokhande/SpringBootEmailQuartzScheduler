package com.amsidh.mvc.SpringBootEmailQuartzScheduler.payload;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class EmailRequest {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String body;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private ZoneId timeZone;

}

