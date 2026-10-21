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

package io.fireflysoftware.domain.users.core.account.handlers;

import io.fireflysoftware.domain.users.core.account.UserAccountPort;
import io.fireflysoftware.domain.users.core.account.StoredUserAccount;
import io.fireflysoftware.domain.users.core.account.commands.UpdateUserAccountCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class UpdateUserAccountHandler extends CommandHandler<UpdateUserAccountCommand, StoredUserAccount> {
    private final UserAccountPort port;

    public UpdateUserAccountHandler(UserAccountPort port) {
        this.port = port;
    }

    @Override
    protected Mono<StoredUserAccount> doHandle(UpdateUserAccountCommand command) {
        return port.update(command.id(), command.draft());
    }
}
