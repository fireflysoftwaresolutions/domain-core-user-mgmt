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
import io.fireflysoftware.domain.users.core.account.commands.RegisterUserAccountCommand;
import io.fireflysoftware.domain.users.core.account.commands.RemoveUserAccountCommand;
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
class RegisterUserAccountSagaTest {
    @Mock
    private CommandBus commandBus;

    @Test
    void registerStoresTheCoreResult() {
        UUID id = UUID.randomUUID();
        StoredUserAccount created = new StoredUserAccount(id, new UserAccountDTO());
        when(commandBus.send(any(RegisterUserAccountCommand.class))).thenReturn(Mono.just(created));
        RegisterUserAccountSaga saga = new RegisterUserAccountSaga(commandBus);
        ExecutionContext ctx = ExecutionContext.forSaga(RegisterUserAccountSaga.NAME, "test");

        StepVerifier.create(saga.register(new RegisterUserAccountCommand(new UserAccountDTO()), ctx))
                .expectNext(created)
                .verifyComplete();
        assertThat(ctx.getVariableAs("created", StoredUserAccount.class).id()).isEqualTo(id);
    }

    @Test
    void removeSendsDeleteForTheCreatedRecord() {
        UUID id = UUID.randomUUID();
        when(commandBus.send(any(RemoveUserAccountCommand.class))).thenReturn(Mono.empty());
        RegisterUserAccountSaga saga = new RegisterUserAccountSaga(commandBus);

        StepVerifier.create(saga.remove(new StoredUserAccount(id, new UserAccountDTO())))
                .verifyComplete();
        ArgumentCaptor<RemoveUserAccountCommand> captor = ArgumentCaptor.forClass(RemoveUserAccountCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
    }
}
