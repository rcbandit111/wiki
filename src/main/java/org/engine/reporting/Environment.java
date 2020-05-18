package org.engine.reporting;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Environment {

	@XmlElement(name = "java_version")
	public String java_version;
	
	@XmlElement(name = "execution_path")
	public String execution_path;
	
	@XmlElement(name = "jvm_uptime")
	public String jvm_uptime;
	
	@XmlElement(name = "ip_address")
	public String ip_address;
	
	@XmlElement(name = "host_name")
	public String host_name;

	public String getJava_version() {
		return java_version;
	}

	public void setJava_version(String java_version) {
		this.java_version = java_version;
	}

	public String getExecution_path() {
		return execution_path;
	}

	public void setExecution_path(String execution_path) {
		this.execution_path = execution_path;
	}

	public String getJvm_uptime() {
		return jvm_uptime;
	}

	public void setJvm_uptime(String jvm_uptime) {
		this.jvm_uptime = jvm_uptime;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}
}
