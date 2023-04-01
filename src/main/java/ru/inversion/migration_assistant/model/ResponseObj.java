package ru.inversion.migration_assistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseObj<T> {
    T result;
}
