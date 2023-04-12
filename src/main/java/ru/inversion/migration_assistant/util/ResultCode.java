package ru.inversion.migration_assistant.util;

public enum ResultCode {
    OK (0L),
    COMMON_ERROR (-1L);

    private final Long resultCode;

    ResultCode(Long resultCode) {
        this.resultCode = resultCode;
    }

    public Long value() {
        return this.resultCode;
    }
}
