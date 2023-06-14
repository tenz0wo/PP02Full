package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.RequestExplainPlan;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScript;
import ru.inversion.migration_assistant.model.response.ResponseExplainPlan;

@Service
@Slf4j
public class TargetDBRepository {

//    public ResponseObj<ResponseExplainPlan> explainPlan(RequestExplainPlan params) {
//        return new DBExecHandler<>(ExplainPlanExecutor.class, new ExecutorParams<>(params)).exec();
//    }
}
