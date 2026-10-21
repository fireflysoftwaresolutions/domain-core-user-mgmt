/*
 * Copyright 2026 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fireflysoftware.domain.users.core.account.workflows;

import io.fireflysoftware.core.users.sdk.model.UserAccountDTO;
import io.fireflysoftware.domain.users.core.account.StoredUserAccount;
import io.fireflysoftware.domain.users.core.account.commands.UpdateUserAccountCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserAccountSagaTest {
    @Mock
    private CommandBus commandBus;

    @Test
    void restoreWritesThePreviousBody() {
        UUID id = UUID.randomUUID();
        UserAccountDTO previousBody = new UserAccountDTO();
        StoredUserAccount previous = new StoredUserAccount(id, previousBody);
        StoredUserAccount updated = new StoredUserAccount(id, new UserAccountDTO());
        when(commandBus.send(any(UpdateUserAccountCommand.class))).thenReturn(Mono.empty());
        UpdateUserAccountSaga saga = new UpdateUserAccountSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga(UpdateUserAccountSaga.NAME, "test");
        ctx.putVariable("previous", previous);

        StepVerifier.create(saga.restore(updated, ctx)).verifyComplete();
        ArgumentCaptor<UpdateUserAccountCommand> captor = ArgumentCaptor.forClass(UpdateUserAccountCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().draft()).isSameAs(previousBody);
    }
}
