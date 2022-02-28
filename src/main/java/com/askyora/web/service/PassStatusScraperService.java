package com.askyora.web.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.askyora.web.dto.EmailRequest;

import lombok.extern.slf4j.Slf4j;
import ru.stqa.selenium.factory.WebDriverPool;

@Component
@Slf4j
public class PassStatusScraperService {

	private WebDriver driver;
	private Map<String, Object> vars;
	JavascriptExecutor js;

	@Value("${to}")
	private String to;

	@Value("${from}")
	String from;

	@Value("${grid-url:http://localhost:4444/wd/hub/}")
	private String gridUrl;

	@Value("${timezone:Asia/Kolkata}")
	String timeZone;

	@Value("${name}")
	String name;

	@Value("${fin}")
	private String ep;

	@Value("${passport}")
	String passport;

	@Value("${dob}")
	private String dob;

	private EmailService emailService;

	Properties prop = new Properties();

	@Autowired
	public PassStatusScraperService(EmailService service) {
		this.vars = new HashMap<String, Object>();
		this.emailService = service;
	}

	private void setUp() throws MalformedURLException {
		try {
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("browserName", "firefox");
			capabilities.setCapability("nativeEvents", false);
			capabilities.setCapability("build", "Vaxx");
			capabilities.setCapability("name", "Vaxx");
			capabilities.setCapability("version", "latest");
			capabilities.setCapability("tunnel", true);
			capabilities.setCapability("network", true);
			capabilities.setCapability("console", true);
			capabilities.setCapability("visual", true);
			capabilities.setPlatform(Platform.LINUX);
			this.driver = WebDriverPool.DEFAULT.getDriver(new URL(gridUrl), capabilities);
			this.js = (JavascriptExecutor) driver;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void tearDown() {
		if (driver != null)
			driver.quit();
	}

	public void passStatusCheck() {
		driver.get("https://eponline.mom.gov.sg/epol/PEPOLENQM007DisplayAction.do");
		driver.manage().window().setSize(new Dimension(1694, 1089));
		{
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.textToBe(By.cssSelector(".pageHead"), "Enquire - Application/Pass Status"));
		}
		driver.findElement(By.name("requesterNRICFIN")).sendKeys(ep);

		driver.findElement(By.name("requesterName")).sendKeys(name);
		driver.findElement(By.name("save")).click();
		{
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.textToBe(By.cssSelector(".pageHead"), "Enquire - Application/Pass Status"));
		}

		driver.findElement(By.name("travelDocNo")).sendKeys(passport);
		driver.findElement(By.name("trvDateBirth")).sendKeys(dob);

		driver.findElement(By.name("submitForm")).click();
		{
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.textToBe(By.cssSelector(".pageHead"), "Enquire - Application/Pass Status"));
		}
		String value = driver.findElement(By.cssSelector("tr:nth-child(6) > td:nth-child(3)")).getText();

		composeAndScheduleEmail(value);
	}

	private void composeAndScheduleEmail(String value) {
		if (Objects.nonNull(value) && !value.isBlank()) {

			ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));
			EmailRequest email = new EmailRequest();
			email.setSubject(String.format("EP Status  for [ %s ]  [ %s ] at [%s] ", ep, value, now));
			email.setBody(String.format("EP Status  for [ %s ]  [ %s ] at [%s] ", ep, value, now));
			email.setEmail(to);
			email.setFrom(from);
			email.setDateTime(LocalDateTime.now());
			email.setTimeZone(ZoneId.of(timeZone));
			emailService.scheduleEmail(email);
		}
	}

	public void run(String id) {
		try {
			log.info("Starting... {}", id);
			setUp();
			passStatusCheck();
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(), ex);

		} finally {
			tearDown();
		}
	}

}
