// src/test/java/com/alpopan/taskmaster/exception/TestExceptionController.java
package com.alpopan.taskmaster.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestExceptionController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new EntityNotFoundException("Test resource not found");
    }

    @PostMapping("/validate")
    public void validate(@RequestBody @Valid TestDto dto) { }

    @GetMapping("/error")
    public void error() {
        throw new RuntimeException("Unexpected exception");
    }

    public static class TestDto {
        @NotBlank(message = "name must not be blank")
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
