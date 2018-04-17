package com.springboot.integration.headerEnricher.intfc;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import com.springboot.integration.headerEnricher.model.Student;

@MessagingGateway(mapper = "mapMethodArgumentsAndEnrichHeaders")
public interface IntegrationAdapterGateway {

	@Gateway(requestChannel = "integration.student.gateway.channel")
	public void sendMessage(Student message);

	@Gateway(requestChannel = "integration.additionalParam.gateway.channel")
	public void sendAdditionalParamWithMessage(Student student, String anotherParam);
}