package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoClientResponse {

    public int code;
    public Data data;
    public String message;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data{
        public int created_at;
        public Object error;
        public String id;
        public String status;
        public Object video_url;
    }


}
