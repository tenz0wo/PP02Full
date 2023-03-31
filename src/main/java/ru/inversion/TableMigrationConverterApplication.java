package ru.inversion;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@OpenAPIDefinition(info = @Info(title = "Table Migration Converter Application", version = "1.0-SNAPSHOT"))
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TableMigrationConverterApplication {
    public static void main(String[] args) {
        SpringApplication.run(TableMigrationConverterApplication.class, args);
    }
}