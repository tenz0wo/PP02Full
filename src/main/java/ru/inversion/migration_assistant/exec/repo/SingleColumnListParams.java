package ru.inversion.migration_assistant.exec.repo;

import lombok.Getter;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.request.RequestParams;
import ru.inversion.migration_assistant.model.sql.ObjectPart;
import ru.inversion.migration_assistant.model.sql.ObjectType;

@Getter
public class SingleColumnListParams<T extends RequestParams> extends ExecutorParams<T> {
    ObjectPart objectPart;
    ObjectType objectType;
    public SingleColumnListParams(T params, ObjectType objectType, ObjectPart objectPart) {
        super(params);
        this.objectPart = objectPart;
        this.objectType = objectType;
    }
}
