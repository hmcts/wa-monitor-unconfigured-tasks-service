package uk.gov.hmcts.reform.wataskmonitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.wataskmonitor.models.JobDetailName;

import java.util.List;

@Component
@Slf4j
public class MonitorTaskJobService {
    private final List<JobService> jobServices;

    @Autowired
    public MonitorTaskJobService(List<JobService> jobServices) {
        this.jobServices = jobServices;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void execute(JobDetailName jobDetailName) {
        jobServices.forEach(job -> {
            if (job.canRun(jobDetailName)) {
                log.info(jobDetailName.name());
                job.run();
            }
        });
    }
}