package org.engine.reporting;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Component
public class ReportingProcessor {
	
	private String URL1 = "https://webhook.site/300b4a52-748b-46ea-8c1f-3bff2a5c1c99";
	
//	@EventListener
	private void collectEnvironmentData(ContextRefreshedEvent event) {
		
		ReportRequest report = new ReportRequest();
		report.setVersion("1.0");
		report.setTimestamp(LocalDateTime.now().toString());
		report.setProduct("Engine");
		report.setProduct_version("1.0");
		
		Environment environment = new Environment();
		environment.setJava_version(getJavVersion());
		environment.setExecution_path(getExecutionPath());
		environment.setJvm_uptime(getUptime());
		environment.setIp_address(getIpAddresses());
		environment.setHost_name(getHostName());
		
		RestClient client = null;
		try {
			client = RestClientBuilder.builder()
					.gatewayUrl(URL1)
//                .token(contract.getTerminal_token())
//			        .usernamePassword("user", "password")
//                .truststore(new File("server.pem"))
//                .sslCertificate(new File("client.pem"), new File("clientKey.p8"), "secret")
					.build();
		} catch (SSLException e) {
			e.printStackTrace();
		}

		Mono<ReportResponse> result = client.executeOnly(report);
		result.block();
		
	}
	
	private String getJavVersion() 
	{
		String version = System.getProperty("java.version");
		if (version == null) {
			version = ManagementFactory.getRuntimeMXBean().getVmVersion();
		}
		return version;
	}
	
	private String getExecutionPath() 
	{
		String executionPath = System.getProperty("user.dir");
		if (executionPath == null) {
			File currentDirectory = new File(new File(".").getAbsolutePath());
			try {
				executionPath = currentDirectory.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return executionPath;
	}
	
	private String getUptime() 
	{
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long uptime = rb.getUptime();
		return String.valueOf(uptime);
	}
	
	private String getIpAddresses() 
	{
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	private String getHostName() {
		
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostName;
	}
}
