package uk.gov.hmcts.reform.wataskmonitor.services.jobs.configuration;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.MetricTelemetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.wataskmonitor.domain.camunda.CamundaTask;
import uk.gov.hmcts.reform.wataskmonitor.domain.jobs.GenericJobOutcome;
import uk.gov.hmcts.reform.wataskmonitor.domain.jobs.GenericJobReport;
import uk.gov.hmcts.reform.wataskmonitor.domain.taskmonitor.JobName;
import uk.gov.hmcts.reform.wataskmonitor.services.JobService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.wataskmonitor.domain.taskmonitor.JobName.CONFIGURATION;
import static uk.gov.hmcts.reform.wataskmonitor.utils.LoggingUtility.logPrettyPrint;

@Slf4j
@Component
public class ConfigurationJob implements JobService {

    static final TelemetryClient telemetryClient = new TelemetryClient();
    private final ConfigurationJobService configurationJobService;

    @Autowired
    public ConfigurationJob(ConfigurationJobService configurationJobService) {
        this.configurationJobService = configurationJobService;
    }

    @Override
    public boolean canRun(JobName jobName) {
        return CONFIGURATION.equals(jobName);
    }

    @Override
    public void run(String serviceToken) {
        log.info("Starting {} job.", CONFIGURATION);
        List<CamundaTask> tasks = configurationJobService.getUnConfiguredTasks(serviceToken);
        GenericJobReport report = configurationJobService.configureTasks(tasks, serviceToken);

        Map<String,String> props = new HashMap<>();
        for(GenericJobOutcome gjo : report.getOutcomeList()){
            if(!gjo.isSuccessful()){
                props.put("taskId", gjo.getTaskId());
            }
        }
        
        telemetryClient.trackEvent("FailingTasks", props, null);
        log.info("{} job finished successfully: {}", CONFIGURATION, logPrettyPrint(report));
    }
}
