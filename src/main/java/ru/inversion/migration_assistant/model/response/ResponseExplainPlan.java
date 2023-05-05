package ru.inversion.migration_assistant.model.response;

import lombok.Data;
import springfox.documentation.spring.web.json.Json;

@Data
public class ResponseExplainPlan {
    String plain_text;
    Json json;
}
