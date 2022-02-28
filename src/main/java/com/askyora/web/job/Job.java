package com.askyora.web.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.askyora.web.service.PassStatusScraperService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Job extends QuartzJobBean {

	private PassStatusScraperService service;

	@Autowired
	public Job(PassStatusScraperService service) {
		this.service = service;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			String id = jobExecutionContext.getJobDetail().getKey().getName();
			service.run(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
	}

}
