package com.example.kinwaeassessment.templates;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleResponse {

    private String status;
    private String message;
}
