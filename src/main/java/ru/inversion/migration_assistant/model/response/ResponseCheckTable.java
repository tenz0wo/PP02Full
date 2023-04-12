package ru.inversion.migration_assistant.model.response;

import lombok.Data;

@Data
public class ResponseCheckTable {
    Boolean tableExists;
    Boolean tableNotEmpty = false;
}
