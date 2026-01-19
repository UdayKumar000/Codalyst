package com.company.demo.services;

import com.company.demo.exceptions.FileProcessingException;
import com.company.demo.exceptions.KrokiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@Slf4j
public class KrokiSvgGenerator {

    @Value("${kroki.base-url}")
    private String krokiUrl;
    private final CloudDatabaseServices cloudDatabaseServices;

    public KrokiSvgGenerator(CloudDatabaseServices cloudDatabaseServices) {
        this.cloudDatabaseServices = cloudDatabaseServices;
    }


    public String getSvg(String mmdFileContent) {

        try{
            HttpClient client = HttpClient.newHttpClient();

            // 3. Build the POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(krokiUrl))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(mmdFileContent))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());


            if (response.statusCode() != 200) {

                String responseError = new String(response.body(), StandardCharsets.UTF_8);
                log.error("Svg Response Code : {}", responseError);
                throw new KrokiException(response.statusCode(), responseError);
            }

            log.info("SVG Generated Successfully");

            return cloudDatabaseServices.uploadSvgToCloud(response.body());

        }catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new KrokiException(500, e.getMessage());
        }
    }

}