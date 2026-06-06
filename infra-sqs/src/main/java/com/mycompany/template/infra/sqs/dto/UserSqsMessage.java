package com.mycompany.template.infra.sqs.dto;

public record UserSqsMessage(String id, String name, String email, String createdAt) {
}
