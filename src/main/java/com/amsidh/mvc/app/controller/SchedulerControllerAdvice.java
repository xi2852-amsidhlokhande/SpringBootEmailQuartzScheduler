package com.amsidh.mvc.app.controller;

import com.amsidh.mvc.app.exception.CustomBusinessException;
import com.amsidh.mvc.app.payload.JobResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class SchedulerControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {SchedulerException.class})
    public ResponseEntity<JobResponse> handleSchedulerException(SchedulerException schedulerException, WebRequest webRequest) {
        log.error("Error while scheduling email: ", schedulerException);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JobResponse(false, "Error while scheduling email. Please try again later!"));
    }

    @ExceptionHandler(value = {CustomBusinessException.class})
    public ResponseEntity<JobResponse> handleCustomBusinessException(CustomBusinessException customBusinessException, WebRequest webRequest) {
        log.error(customBusinessException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JobResponse(false, customBusinessException.getLocalizedMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDate.now());
        body.put("status", status.value());
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
