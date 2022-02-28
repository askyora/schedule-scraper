package com.askyora.web.dto;

import lombok.Data;

@Data
public class EmailResponse {
	public EmailResponse(boolean success, String message) {
		this.success = success;
		this.message = message;

	}

	public EmailResponse(boolean success, String jobId, String group, String message) {
		this(success, message);
		this.jobGroup=group;
		this.jobId=jobId;
	}

	private boolean success;
	private String jobId;
	private String jobGroup;
	private String message;
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
