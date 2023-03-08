package uk.gov.hmcts.reform.wataskmonitor.domain.taskmonitor;

public enum JobName {
    TERMINATION, INITIATION, AD_HOC_DELETE_PROCESS_INSTANCES,
    AD_HOC_PENDING_TERMINATION_TASKS, TASK_INITIATION_FAILURES, TASK_TERMINATION_FAILURES,
    RECONFIGURATION, MAINTENANCE_CAMUNDA_TASK_CLEAN_UP,
    CLEANUP_SENSITIVE_LOG_ENTRIES
}
