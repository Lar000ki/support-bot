package com.bob.support_platform.web;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;


@Service
@Slf4j
public class YamlConfigService {

    private final Path configPath = Path.of("application.yml");
    private final ObjectMapper mapper;

    public YamlConfigService() {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.setPropertyNamingStrategy(
                PropertyNamingStrategies.KEBAB_CASE
        );
    }

    public SupportConfigDto read() {
        try (InputStream is = Files.newInputStream(configPath)) {
            return mapper.readValue(is, SupportConfigDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read .yml", e);
        }
    }

    public void write(SupportConfigDto dto) {
        try (OutputStream os = Files.newOutputStream(
                configPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            mapper.writeValue(os, dto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write .yml", e);
        }
    }
}
