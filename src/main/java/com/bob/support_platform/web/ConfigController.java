package com.bob.support_platform.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class ConfigController {

    private final YamlConfigService configService;

    @GetMapping
    public SupportConfigDto getConfig() {
        return configService.read();
    }

    @PostMapping
    public ResponseEntity<?> saveConfig(@RequestBody @Valid SupportConfigDto dto) {
        configService.write(dto);
        return ResponseEntity.ok(
                Map.of("message", "saved")
        );
    }
}

