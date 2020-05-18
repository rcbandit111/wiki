package org.engine.reporting;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class RestClientBuilder {
    private String token;
    private String username;
    private String password;
    private URL gatewayUrl;
    private SslContextBuilder sslContextBuilder;

    public static RestClientBuilder builder() {
        return new RestClientBuilder();
    }

    /**
     * @param token authentication token
     */
    public RestClientBuilder token(String token) {
        this.token = validateAndTrim(token, "Token");
        return this;
    }

    public RestClientBuilder usernamePassword(String username, String password) {
        this.username = validateAndTrim(username, "Username");
        this.password = validateAndTrim(password, "Password");
        return this;
    }

    private String validateAndTrim(String value, final String parameter) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(parameter + " is empty");
        }
        return value.trim();
    }

    /**
     * The gateway URL
     *
     * @param gatewayUrl the gateway URL i.e. https://localhost:8443
     */
    public RestClientBuilder gatewayUrl(String gatewayUrl) {
        String urlSt = validateAndTrim(gatewayUrl, "Gateway URL");
        try {
            this.gatewayUrl = new URL(urlSt);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + urlSt, e);
        }
        return this;
    }

    /**
     * Trusted certificates for verifying the remote endpoint's certificate. The file should
     * contain an X.509 certificate collection in PEM format. {@code null} uses the system default.
     */
    public RestClientBuilder truststore(File truststoreFile) {
        getSslContextBuilder().trustManager(truststoreFile);
        return this;
    }

    /**
     * Set client SSL certificate
     *
     * @param keyCertChainFile an X.509 certificate chain file in PEM format
     * @param keyFile          a PKCS#8 private key file in PEM format
     * @param keyPassword      the password of the {@code keyFile}, or {@code null} if it's not
     *                         password-protected
     */
    public RestClientBuilder sslCertificate(File keyCertChainFile, File keyFile, String keyPassword) {
        getSslContextBuilder().keyManager(keyCertChainFile, keyFile, keyPassword);
        return this;
    }

    public RestClient build() throws SSLException {
        SslContext sslContext = sslContextBuilder != null ? sslContextBuilder.build() : null;
        return new RestClient(gatewayUrl.toString(), token, username, password, sslContext);
    }

    private SslContextBuilder getSslContextBuilder() {
        if (sslContextBuilder == null) {
            sslContextBuilder = SslContextBuilder.forClient();
        }
        return sslContextBuilder;
    }

}
