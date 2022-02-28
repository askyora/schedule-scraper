package com.askyora.web.service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.askyora.web.config.EmailJob;
import com.askyora.web.dto.EmailRequest;
import com.askyora.web.dto.EmailResponse;

@Component
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailJob.class);

	@Autowired
	private Scheduler scheduler;

	public EmailResponse scheduleEmail(@Valid EmailRequest request) {
		 log.info("email {}", request.toString());
		try {
			ZonedDateTime dateTime = ZonedDateTime.of(request.getDateTime(), request.getTimeZone());
			JobDetail jobDetail = buildJobDetail(request);
			Trigger trigger = buildJobTrigger(jobDetail, dateTime);
			scheduler.scheduleJob(jobDetail, trigger);

			EmailResponse scheduleEmailResponse = new EmailResponse(true, jobDetail.getKey().getName(),
					jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
			return scheduleEmailResponse;
		} catch (SchedulerException ex) {
			 log.error("Error scheduling email", ex);

			EmailResponse scheduleEmailResponse = new EmailResponse(false, "Error scheduling email. Please try later!");
			return scheduleEmailResponse;
		}
	}
	
	
	

	private JobDetail buildJobDetail(EmailRequest request) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("email", request.getEmail());
		jobDataMap.put("subject", request.getSubject());
		jobDataMap.put("body", request.getBody());
		jobDataMap.put("from", request.getFrom());

		return JobBuilder.newJob(EmailJob.class).withIdentity(UUID.randomUUID().toString(), "email-jobs")
				.withDescription("Send Email Job").usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "email-triggers").withDescription("Send Email Trigger")
				.startAt(Date.from(ZonedDateTime.now().toInstant()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
	}
}
