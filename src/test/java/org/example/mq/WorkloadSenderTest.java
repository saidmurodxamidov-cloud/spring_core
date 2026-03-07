package org.example.mq;

import org.example.dto.request.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadSenderTest {

    @Mock
    private WorkloadSenderDelegate delegate;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    private WorkloadSender workloadSender;
    private TrainerWorkloadRequest workloadRequest;

    @BeforeEach
    void setUp() {
        workloadSender = new WorkloadSender(delegate, idempotencyKeyService);

        workloadRequest = new TrainerWorkloadRequest();
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setUsername("trainer1");
    }

    @Test
    void sendWorkload_GeneratesKeyAndDelegates() {
        when(idempotencyKeyService.generateKey(workloadRequest)).thenReturn("key123");

        workloadSender.sendWorkload(workloadRequest);

        verify(idempotencyKeyService).generateKey(workloadRequest);
        verify(delegate).sendWorkload(workloadRequest, "key123");
    }

    @Test
    void sendWorkload_DifferentRequestsProduceDifferentKeys() {
        TrainerWorkloadRequest request1 = new TrainerWorkloadRequest();
        request1.setActionType(ActionType.ADD);
        request1.setUsername("trainer1");

        TrainerWorkloadRequest request2 = new TrainerWorkloadRequest();
        request2.setActionType(ActionType.DELETE);
        request2.setUsername("trainer2");

        when(idempotencyKeyService.generateKey(request1)).thenReturn("key1");
        when(idempotencyKeyService.generateKey(request2)).thenReturn("key2");

        workloadSender.sendWorkload(request1);
        workloadSender.sendWorkload(request2);

        verify(delegate).sendWorkload(request1, "key1");
        verify(delegate).sendWorkload(request2, "key2");
    }
}