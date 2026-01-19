package com.company.demo.services;

import com.company.demo.exceptions.DatabaseExceptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CloudDatabaseServices {

    @Value("${superbase.api.base-url}")
    private String SUPABASE_URL;

    @Value("${superbase.api.service-key}")
    private String SERVICE_ROLE_KEY;

    @Value("${superbase.api.bucket-name}")
    private String BUCKET_NAME;

    private final ProjectDatabaseService projectDatabaseService;

    public CloudDatabaseServices(ProjectDatabaseService projectDatabaseService) {
        this.projectDatabaseService = projectDatabaseService;
    }


    public String uploadSvgToCloud(byte[] fileData) {

        try {

            OkHttpClient client = new OkHttpClient();

            if (fileData == null) {
                throw new NullPointerException("file content is null");
            }
            RequestBody body = RequestBody.create(fileData, MediaType.get("image/svg+xml"));

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + UUID.randomUUID().toString() + ".svg")
                    .post(body)
                    .header("Authorization", "Bearer " + SERVICE_ROLE_KEY)
                    .header("x-upsert", "true") // Overwrites if file exists
                    .build();

            Response response = client.newCall(request).execute();


            if (response.body() == null) {
                throw new DatabaseExceptions("Failed to upload files", null);
            }


            String json = response.body().string();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);

            String key = rootNode.get("Key").asText();

            return SUPABASE_URL + "/storage/v1/object/" + key;
        }catch(Exception e){
            log.error("error while uploading file to cloud ",e);
            throw new DatabaseExceptions("Cloud storage uploading error",e);
        }

    }

    public String uploadFileToCloud(String fileContent)  {

        try{
            OkHttpClient client = new OkHttpClient();

            if(fileContent==null || fileContent.trim().isEmpty()){
                throw new NullPointerException("file content is null");
            }
            RequestBody body = RequestBody.create(
                    fileContent,
                    MediaType.parse("text/plain")
            );


            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/storage/v1/object/"+BUCKET_NAME+"/"+ UUID.randomUUID().toString()+".txt")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + SERVICE_ROLE_KEY)
                    .build();


            Response response = client.newCall(request).execute();

            if(response.body()==null){
                throw new DatabaseExceptions("Failed to upload files",null);
            };

            String json = response.body().string();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);

            String key = rootNode.get("Key").asText();

            return SUPABASE_URL + "/storage/v1/object/"+key;

        }catch(Exception e){
            log.error("error while uploading file to cloud ",e);
            throw new DatabaseExceptions("Cloud storage uploading error",e);
        }


    }


}
