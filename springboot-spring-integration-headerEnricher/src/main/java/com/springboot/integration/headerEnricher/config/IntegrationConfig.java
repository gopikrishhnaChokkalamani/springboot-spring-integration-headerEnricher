package com.springboot.integration.headerEnricher.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.gateway.MethodArgsHolder;
import org.springframework.integration.gateway.MethodArgsMessageMapper;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.StaticHeaderValueMessageProcessor;
import org.springframework.messaging.Message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.integration.headerEnricher.model.Student;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class IntegrationConfig {

	@Bean
	@Transformer(inputChannel = "integration.student.toConvertObject.channel", outputChannel = "integration.student.objectToJson.channel")
	public ObjectToJsonTransformer objectToJsonTransformer() {
		return new ObjectToJsonTransformer(getMapper());
	}

	@Bean
	public Jackson2JsonObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		return new Jackson2JsonObjectMapper(mapper);
	}

	@Bean
	@Transformer(inputChannel = "integration.student.jsonToObject.channel", outputChannel = "integration.student.jsonToObject.fromTransformer.channel")
	public JsonToObjectTransformer jsonToObjectTransformer() {
		return new JsonToObjectTransformer(Student.class);
	}

	@Bean
	@Transformer(inputChannel = "integration.student.gateway.channel", outputChannel = "integration.student.toConvertObject.channel")
	public HeaderEnricher enrichHeader() {
		Map<String, HeaderValueMessageProcessor<?>> headersToAdd = new HashMap<>();
		headersToAdd.put("header1", new StaticHeaderValueMessageProcessor<String>("Header1"));
		headersToAdd.put("header2", new StaticHeaderValueMessageProcessor<String>("Header1"));
		HeaderEnricher enricher = new HeaderEnricher(headersToAdd);
		return enricher;
	}

	@Bean
	public MethodArgsMessageMapper mapMethodArgumentsAndEnrichHeaders() {
		return new MethodArgsMessageMapper() {
			@Override
			public Message<?> toMessage(MethodArgsHolder object) throws Exception {
				MutableMessageHeaders headers = new MutableMessageHeaders(new HashMap<>());
				headers.put("someRandomId", UUID.randomUUID().toString());
				headers.put("methodArgument", object.getArgs()[1]);
				return org.springframework.messaging.support.MessageBuilder.createMessage(object.getArgs()[0], headers);
			}
		};
	}
}