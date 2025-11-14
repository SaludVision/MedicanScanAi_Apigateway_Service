package com.saludvision.medicanscanai.medicanscanai_apigateway.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Enumeration;
import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class GatewayController {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request) throws Exception {
        return forwardRequest(request, "http://localhost:8081/api/v1/iam/login");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(HttpServletRequest request) throws Exception {
        return forwardRequest(request, "http://localhost:8081/api/v1/iam/register");
    }

    private ResponseEntity<String> forwardRequest(HttpServletRequest request, String targetUrl)
            throws IOException, InterruptedException {

        // Leer el cuerpo (body)
        String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);

        // Construir la solicitud hacia el microservicio
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .method(request.getMethod(),
                        body.isEmpty() ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));

        // Lista de headers prohibidos que NO se pueden setear manualmente
        List<String> restrictedHeaders = List.of(
                "host", "content-length", "connection", "expect", "date", "transfer-encoding", "trailer"
        );

        // Copiar headers válidos del request original
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toLowerCase();
            if (!restrictedHeaders.contains(headerName)) {
                builder.header(headerName, request.getHeader(headerName));
            }
        }

        // Enviar la solicitud al microservicio IAM
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        // Devolver la respuesta original
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }
}
