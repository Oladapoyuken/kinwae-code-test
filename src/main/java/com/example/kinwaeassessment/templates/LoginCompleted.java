package com.example.kinwaeassessment.templates;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginCompleted {
    private String status;
    private String message;
    private Long userId;
}
