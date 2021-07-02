package uk.gov.hmcts.reform.wataskmonitor.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.wataskmonitor.models.MonitorTaskJobReq;
import uk.gov.hmcts.reform.wataskmonitor.services.MonitorTaskJobService;

@RestController
@Slf4j
public class MonitorTaskJobController {

    private final MonitorTaskJobService monitorTaskJobService;

    @Autowired
    public MonitorTaskJobController(MonitorTaskJobService monitorTaskJobService) {
        this.monitorTaskJobService = monitorTaskJobService;
    }

    @PostMapping("/monitor/tasks/jobs")
    public MonitorTaskJobReq monitorTaskJob(@RequestBody MonitorTaskJobReq monitorTaskJobReq) {
        log.info("Received request to create a new job of type '{}'", monitorTaskJobReq.getJobDetails().getName());
        monitorTaskJobService.execute(monitorTaskJobReq.getJobDetails().getName());
        log.info("Job({}) processed in the background", monitorTaskJobReq.getJobDetails().getName());
        return monitorTaskJobReq;
    }

}
