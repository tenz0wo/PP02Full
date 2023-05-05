package ru.inversion.migration_assistant.model.request;

import lombok.Data;

@Data
public class CallbackExplainPlan {
    boolean plain_text;
    boolean json;
}
