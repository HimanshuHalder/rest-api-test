package com.eo.rest.test.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatedResponse {
    public String id;
    public String createdAt;
    public String name;
    public String job;
}
