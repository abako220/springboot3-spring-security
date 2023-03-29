package com.troy.security.demo;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class DemoController {

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<String> sayHello(HttpServletResponse response) {
        response.setContentType("text/json");
        return ResponseEntity.ok("Hello from secured endpoint");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/hello1")
    public ResponseEntity<String> sayHello1() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }

    @PreAuthorize(
            "hasRole('ROLE_ADMIN') " +
            "or " +
                    "hasAnyRole('ROLE_USER')")
    @GetMapping("/hello2")
    public ResponseEntity<String> sayHello2() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
