package com.askyora.web.config;

import java.util.TimeZone;
import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.askyora.web.job.Job;

@Configuration
public class JobConfig {

	@Value("${am.hour:9}")
	private int amHour;
	@Value("${am.mintus:0}")
	private int amMin;

	@Value("${pm.hour:18}")
	private int pmHour;
	@Value("${pm.mintus:0}")
	private int pmMin;

	@Value("${timezone:IST}")
	private String timeZone = "IST";

	@Bean
	public JobDetail create() {
		return JobBuilder.newJob(Job.class).withIdentity("EP_STATUS-JOB").storeDurably().build();
	}

	@Bean(name = "AM")
	public Trigger createTrigger10(JobDetail jobADetails) {

		return TriggerBuilder.newTrigger().forJob(jobADetails)
				.withIdentity("Trigger AM :" + UUID.randomUUID().toString()).withSchedule(CronScheduleBuilder
						.dailyAtHourAndMinute(amHour, amMin).inTimeZone(TimeZone.getTimeZone(timeZone)))
				.build();
	}

	@Bean(name = "PM")
	public Trigger createTrigger7(JobDetail jobADetails) {
		return TriggerBuilder.newTrigger().forJob(jobADetails)
				.withIdentity("Trigger PM :" + UUID.randomUUID().toString()).withSchedule(CronScheduleBuilder
						.dailyAtHourAndMinute(pmHour, pmMin).inTimeZone(TimeZone.getTimeZone(timeZone)))
				.build();
	}

}
