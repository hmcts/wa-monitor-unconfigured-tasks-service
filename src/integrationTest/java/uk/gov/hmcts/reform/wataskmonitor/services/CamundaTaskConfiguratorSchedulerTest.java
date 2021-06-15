package uk.gov.hmcts.reform.wataskmonitor.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.wataskmonitor.schedulers.TaskConfiguratorScheduler;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("integration")
class CamundaTaskConfiguratorSchedulerTest {

    @MockBean
    private CamundaService camundaService;
    @MockBean
    private TaskConfigurationService taskConfigurationService;

    @SpyBean
    private TaskConfiguratorScheduler taskConfiguratorScheduler;

    @Test
    void runTaskConfigurator() {
        assertTaskConfiguratorRunsEveryTenSeconds();
    }

    private void assertTaskConfiguratorRunsEveryTenSeconds() {
        await().atMost(12, TimeUnit.SECONDS)
            .untilAsserted(
                () -> {
                    verify(taskConfiguratorScheduler, times(2)).runTaskConfigurator();
                    verify(camundaService, times(2)).getUnConfiguredTasks();
                    verify(taskConfigurationService, times(2)).configureTasks(anyList());
                });
    }
}