package ma.ip.config.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import ma.ip.dto.accounting.request.AccountingRootRequest;
import ma.ip.exceptions.ProxyRequestException;
import ma.ip.services.TokenApigeeService;
import ma.ip.utils.SecurityUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class ApigeeRequestInterceptor {

    @Value("${ip.apigee.ssl.trustSelfSignedStrategy:false}")
    private boolean trustSelfSignedStrategy;
    @Value("${ip.apigee.ssl.mtls:false}")
    private boolean mtls;
    @Value("${ip.apigee.timeout:60000}")
    private int timeout;
    @Value("${ip.apigee.ssl.keyStorePath:}")
    private String keyStorePath;
    @Value("${ip.apigee.ssl.keyStorePassword:}")
    private String keyStorePassword;
    @Value("${ip.apigee.ssl.keyStoreType:JKS}")
    private String keyStoreType;
    @Value("${ip.apigee.ssl.trustStorePath:}")
    private String trustStorePath;
    @Value("${ip.apigee.ssl.trustStorePassword:}")
    private String trustStorePassword;
    @Value("${ip.apigee.ssl.trustStoreType:JKS}")
    private String trustStoreType;

    @Value("${ip.apigee.max.connections.total:100}")
    private int maxConn;

    @Value("${ip.apigee.max.per.route:50}")
    private int maxPerRoute;
    @Value("${ip.authorization.header:Authorization}")
    private String authorizationHeader;
    @Value("${ip.request.id.header:X-Request-Id}")
    private String requestId;


    @Value("${ip.request.signature.header:X-Timestamp}")
    private String xTimestamp;
    @Value("${ip.request.signature.header:X-Signature}")
    private String xSignature;
    @Value("${ip.request.signature.secret:}")
    private String secret;

    @Value("${ip.request.apikey.header:X-API-Key}")
    private String xApiKey;
    @Value("${ip.request.apikey.value:}")
    private String xApiKeyValue;

    @Value("${ip.request.content.header:Content-Type}")
    private String contentType;

    @Value("${ip.request.accept.header:accept}")
    private String accept;
    @Value("${ip.request.partnerid.header:PartnerId}")
    private String partnerId;
    @Value("${ip.request.version.header:Version}")
    private String version;

    @Value("${ip.request.accept.value:}")
    private String acceptValue;
    @Value("${ip.request.partnerid.value:}")
    private String partnerIdHeaderValue;
    @Value("${ip.request.version.value:}")
    private String versionHeadValue;

    @Autowired
    private TokenApigeeService tokenApigeeService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Tracer tracer;

    @Bean
    public Client feignClient() {
        return new ApacheHttpClient(getHttpClient());
    }


    private KeyStore keyStore(String file, char[] password, String type) throws Exception {
        // type JKS,PKCS12
        KeyStore keyStore = KeyStore.getInstance(type);
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }

    private CloseableHttpClient getHttpClient() {
        try {
            SSLContext sslContext = null;
            SSLContextBuilder sslContextBuilder = null;
            if (mtls) {
                KeyStore keyStore = keyStore(keyStorePath, keyStorePassword.toCharArray(), keyStoreType);
                sslContextBuilder = SSLContextBuilder.create().loadKeyMaterial(keyStore, keyStorePassword.toCharArray());
                KeyStore trustStore = keyStore(trustStorePath, trustStorePassword.toCharArray(), trustStoreType);
                if (trustSelfSignedStrategy) {
                    sslContextBuilder = sslContextBuilder.loadTrustMaterial(trustStore, TrustSelfSignedStrategy.INSTANCE);
                } else {
                    sslContextBuilder = sslContextBuilder.loadTrustMaterial(trustStore, null);
                }
            }
            if (sslContextBuilder != null) {
                sslContext = sslContextBuilder.build();
            }

            RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().useSystemProperties().setDefaultRequestConfig(config).setMaxConnTotal(maxConn).setMaxConnPerRoute(maxPerRoute);

            if (sslContext != null) {
                httpClientBuilder = httpClientBuilder.setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier());
            }
            return httpClientBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Bean
    public RequestInterceptor requestInterceptor() {

        Span span = tracer.currentSpan();

        return requestTemplate -> {
            System.out.println("REQUEST INTERCEPTOR APIGEE API");
            requestTemplate.removeHeader(requestId);
            requestTemplate.removeHeader(authorizationHeader);
            requestTemplate.removeHeader(contentType);
            requestTemplate.removeHeader(xTimestamp);
            requestTemplate.removeHeader(xSignature);
            requestTemplate.removeHeader(xApiKey);
            requestTemplate.header(contentType, "application/json");
            requestTemplate.header(accept,acceptValue);
            requestTemplate.header(version, versionHeadValue);
            requestTemplate.header(partnerId, partnerIdHeaderValue);

            final long timestamp = System.currentTimeMillis();
            requestTemplate.header(xTimestamp, String.valueOf(timestamp));
            StringBuilder requestAsStringBuilder = new StringBuilder();
            requestAsStringBuilder.append(xApiKeyValue);
            requestAsStringBuilder.append("|");
            requestAsStringBuilder.append(timestamp);
            requestAsStringBuilder.append("|");
            final String httpMethod = requestTemplate.method();
            requestAsStringBuilder.append(httpMethod);
            requestAsStringBuilder.append(":");
            requestAsStringBuilder.append(requestTemplate.path());
            requestAsStringBuilder.append("|");

            final byte[] bytes = requestTemplate.body();
            if (bytes == null)
                System.out.println("request body is null");
            else {
                final String bodyAsString = new String(bytes, StandardCharsets.UTF_8);
                requestAsStringBuilder.append(bodyAsString);
            }
            System.out.println("Request As String: " + requestAsStringBuilder.toString());
            String xSignatureValue = SecurityUtils.getRequestSignature(requestAsStringBuilder.toString(), secret);
            requestTemplate.header(xSignature, xSignatureValue);
            requestTemplate.header(xApiKey, xApiKeyValue);
            //requestTemplate.uri(requestTemplate.request().url().replace("%2B", "+"));
        };
    }

    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
//        return new Retryer.Default(retryerPeriod, TimeUnit.SECONDS.toMillis(retryerMaxPeriod), retryerMaxAttempts);
    }

    @Bean
    public ErrorDecoder CustomErrorDecoder() {

        return (methodKey, response) -> {
            FeignException exception = FeignException.errorStatus(methodKey, response);
//            System.out.println("Retry =" + response.status());
//            if (response.status() == 401) {
//                String token = tokenApigeeService.refreshToken();
//                RequestTemplate requestTemplate = response.request().requestTemplate();
//                Request request = requestTemplate.removeHeader(authorizationHeader).header(authorizationHeader, token).request();
//                return new RetryableException(response.status(), exception.getMessage(), response.request().httpMethod(), new Date(), request);
//            }
            // BadRequest
            if (response.status() == 400) {
                String body = "{}";
                try {
                    body = exception.contentUTF8();
                } catch (Exception ignored) {
                }
                Map<String, String> responseBody = null;
                try {
                    responseBody = mapper.readValue(body, Map.class);
                } catch (JsonProcessingException e) {
                }
                HttpHeaders httpHeaders = new HttpHeaders();
                response.headers().forEach((k, v) -> httpHeaders.add("feign-" + k, StringUtils.join(v, ',')));
                return new ProxyRequestException(responseBody, response.status());
            }
            if (response.status() == 401) {
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("code", "401");
                responseBody.put("message", "Authentication failed");
                HttpHeaders httpHeaders = new HttpHeaders();
                response.headers().forEach((k, v) -> httpHeaders.add("feign-" + k, StringUtils.join(v, ',')));
                return new ProxyRequestException(responseBody, response.status());
            }
            // Timeout
            if (response.status() == 408) {
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("code", "408");
                responseBody.put("message", "Timeout");
                return new ProxyRequestException(responseBody, response.status());
            }
//            if (response.status() == 503) {
//                return new RetryableException(response.status(), exception.getMessage(), response.request().httpMethod(), exception, null, response.request());
//            }
//            System.out.println(" Not Retry =" + response.status());

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("code", String.valueOf(response.status()));
            responseBody.put("message", String.valueOf(response.status()));
            return new ProxyRequestException(responseBody, response.status());
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // NONE, BASIC, HEADERS, FULL
    }
}
