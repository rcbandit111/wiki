package org.engine.reporting;

import io.netty.handler.ssl.SslContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

public class RestClient {
	
    private WebClient client;
    private String gatewayUrl;

    /**
     * Creates RestClient. Please use RestClientBuilder for defining SSL context
     *
     * @param gatewayUrl the gateway URL
     * @param token      the authentication token
     * @param sslContext ssl context. If null then the default is used.
     */
    public RestClient(String gatewayUrl, String token, String username, String password, SslContext sslContext) {
        this.gatewayUrl = gatewayUrl;
        WebClient.Builder builder = WebClient.builder().baseUrl(gatewayUrl);
        if (sslContext != null) {
        	HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        	ClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClient);
            builder.clientConnector(httpConnector);
        }
        if (username != null && password != null) {
            builder.filter(basicAuthentication(username, password));
        }
        client = builder.build();
    }
    
    public Mono<ReportResponse> executeOnly(ReportRequest transaction) {
        Mono<ReportRequest> transactionMono = Mono.just(transaction);
        return client.post().uri(gatewayUrl)
        		.header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML)
                .body(transactionMono, ReportRequest.class)
                .retrieve()
                .bodyToMono(ReportResponse.class);
    }
    
    public Mono<ReportResponse> executeAndReceiveReport(ReportRequest transaction) {
        Mono<ReportRequest> transactionMono = Mono.just(transaction);
        return client.post().uri(gatewayUrl)
        		.header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.APPLICATION_XML)
                .body(transactionMono, ReportRequest.class)
                .retrieve()
                .bodyToMono(ReportResponse.class);
    }
     
    private String checkTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }
}
