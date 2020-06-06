package org.engine.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class ErrorResponse {

	private int status;

	private String code;

	private String title;

	private String detail;

	private ErrorSource source;

	private String debugDetail;

	private Map<String, String> extra;

	public ErrorResponse(String code, String title, Integer status){
		this.code = code;
		this.title = title;
		this.status = status;
	}

	public ErrorResponse(String code, String title){
		this.code = code;
		this.title = title;
	}

	public ErrorResponse(String code, String title, String detail, Integer status){
		this.code = code;
		this.title = title;
		this.detail = detail;
		this.status = status;
	}

	public ErrorResponse(String code, String title, Integer status, Map<String, String> extra){
		this(code, title, status);
		this.extra = extra;
	}

	public ErrorResponse(){
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public void setSource(ErrorSource source) {
		this.source = source;
	}

	public void setDebugDetail(String debugDetail) {
		this.debugDetail = debugDetail;
	}

	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}
}
