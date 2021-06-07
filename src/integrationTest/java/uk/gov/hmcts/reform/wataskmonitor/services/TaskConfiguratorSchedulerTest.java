package uk.gov.hmcts.reform.wataskmonitor.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TaskConfiguratorSchedulerTest {

    @SpyBean
    private TaskConfiguratorScheduler taskConfiguratorScheduler;

    @Test
    void runTaskConfigurator() {
        await().atMost(12, TimeUnit.SECONDS)
            .untilAsserted(
                () -> verify(taskConfiguratorScheduler, times(2)).runTaskConfigurator());
    }
}
