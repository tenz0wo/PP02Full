package ru.inversion.migration_assistant.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbObjectWithSchema {
    String obj;
    String schema;
}
