package org.engine.service;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmailModel {
	
	private String mailFrom;

	private String mailTo;
	
	private String mailSubject;
	
	private String mailContent;
	
	private Map<String, Object> model;
}
