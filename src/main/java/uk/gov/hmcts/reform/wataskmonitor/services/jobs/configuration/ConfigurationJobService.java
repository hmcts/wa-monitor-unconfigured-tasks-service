package uk.gov.hmcts.reform.wataskmonitor.services.jobs.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.wataskmonitor.clients.CamundaClient;
import uk.gov.hmcts.reform.wataskmonitor.clients.TaskConfigurationClient;
import uk.gov.hmcts.reform.wataskmonitor.domain.camunda.CamundaTask;
import uk.gov.hmcts.reform.wataskmonitor.utils.ResourceUtility;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static uk.gov.hmcts.reform.wataskmonitor.services.jobs.RequestsEnum.CONFIGURATION_JOB_SERVICE;


@Component
@Slf4j
public class ConfigurationJobService {

    public static final String CAMUNDA_DATE_REQUEST_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS+0000";

    private final CamundaClient camundaClient;
    private final TaskConfigurationClient taskConfigurationClient;

    @Autowired
    public ConfigurationJobService(CamundaClient camundaClient, TaskConfigurationClient taskConfigurationClient) {
        this.camundaClient = camundaClient;
        this.taskConfigurationClient = taskConfigurationClient;
    }

    public List<CamundaTask> getUnConfiguredTasks(String serviceToken) {
        log.info("Retrieving unconfigured tasks from camunda.");
        List<CamundaTask> camundaTasks = camundaClient.getTasks(
            serviceToken,
            "0",
            "1000",
            getQueryParameters()
        );
        log.info("{} task(s) retrieved successfully.", camundaTasks.size());
        return camundaTasks;
    }


    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void configureTasks(List<CamundaTask> camundaTasks, String serviceToken) {
        if (camundaTasks.isEmpty()) {
            log.info("There was no task(s) to configure.");
        } else {
            log.info("Attempting to configure {} task(s)", camundaTasks.size());
            camundaTasks.forEach(task -> {
                try {
                    log.info("Attempting to configure task with id: '{}'", task.getId());
                    taskConfigurationClient.configureTask(serviceToken, task.getId());
                    log.info("Task with id: '{}' configured successfully.", task.getId());
                } catch (Exception e) {
                    log.info("Error while configuring task with id: '{}'", task.getId());
                }
            });
        }
    }

    private String getQueryParameters() {
        return ResourceUtility.getResource(CONFIGURATION_JOB_SERVICE.getRequestParameterBody())
            .replace("CREATED_BEFORE_PLACEHOLDER", getCreatedBeforeDate());
    }

    private static String getCreatedBeforeDate() {
        return ZonedDateTime.now()
            .minusMinutes(5)
            .format(DateTimeFormatter.ofPattern(CAMUNDA_DATE_REQUEST_PATTERN));
    }

}