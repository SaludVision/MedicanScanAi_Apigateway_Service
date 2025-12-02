package com.saludvision.medicanscanai.medicanscanai_apigateway;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Enumeration;

@RestController
@RequestMapping("/api/v1")
public class GatewayController {

    private final RestClient restClient;
    
    @Value("${iam.service.url}")
    private String iamServiceUrl;

    public GatewayController(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @PostMapping("/iam/**")
    public ResponseEntity<?> forwardToIam(@RequestBody(required = false) Object body,
                                           HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String targetUrl = iamServiceUrl + requestPath;
        
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("host") && !headerName.equalsIgnoreCase("content-length")) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }
        
        return restClient.post()
                .uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(body != null ? body : "")
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/iam/**")
    public ResponseEntity<?> forwardToIamGet(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String targetUrl = iamServiceUrl + requestPath;
        
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("host") && !headerName.equalsIgnoreCase("content-length")) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }
        
        return restClient.get()
                .uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .toEntity(Object.class);
    }
}

