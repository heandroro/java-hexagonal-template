package com.mycompany.template.infra.client.feign;

import com.mycompany.template.infra.client.feign.dto.ExternalUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "external-user-service", url = "${app.clients.external-user-service.url}")
public interface ExternalUserClient {

    @GetMapping("/users/{id}")
    ExternalUserResponse findById(@PathVariable UUID id);
}
