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

import io.fireflysoftware.domain.users.core.account.StoredUserAccount;
import io.fireflysoftware.domain.users.core.account.commands.RegisterUserAccountCommand;
import io.fireflysoftware.domain.users.core.account.commands.RemoveUserAccountCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/** Creates the record and deletes it if a later step fails. */
@Saga(name = RegisterUserAccountSaga.NAME)
@Service
public class RegisterUserAccountSaga {
    public static final String NAME = "RegisterUserAccountSaga";
    public static final String STEP = "register";
    public static final String COMPENSATE = "remove";

    private final CommandBus commandBus;

    public RegisterUserAccountSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP, compensate = COMPENSATE)
    @StepEvent(type = "users.account.registered")
    public Mono<StoredUserAccount> register(RegisterUserAccountCommand command, ExecutionContext ctx) {
        return commandBus.<StoredUserAccount>send(command)
                .doOnNext(created -> ctx.putVariable("created", created));
    }

    public Mono<Void> remove(StoredUserAccount created) {
        if (created == null || created.id() == null) {
            return Mono.empty();
        }
        return commandBus.send(new RemoveUserAccountCommand(created.id()));
    }
}
