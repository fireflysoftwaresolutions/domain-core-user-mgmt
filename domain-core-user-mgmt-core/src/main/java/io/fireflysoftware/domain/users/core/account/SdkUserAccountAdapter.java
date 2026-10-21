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

package io.fireflysoftware.domain.users.core.account;

import io.fireflysoftware.core.users.sdk.api.UserAccountsApi;
import io.fireflysoftware.core.users.sdk.model.UserAccountDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/** Live adapter. Selected with firefly.user.account.adapter=core. */
public final class SdkUserAccountAdapter implements UserAccountPort {
    private final UserAccountsApi api;

    public SdkUserAccountAdapter(UserAccountsApi api) {
        this.api = api;
    }

    @Override
    public Mono<UserAccountView> read(UUID id) {
        return api.getUserAccountById(id, null).map(dto -> new UserAccountView(dto.getId(), dto.getEmail()));
    }

    @Override
    public Mono<StoredUserAccount> load(UUID id) {
        return api.getUserAccountById(id, null).map(dto -> new StoredUserAccount(dto.getId() == null ? id : dto.getId(), dto));
    }

    @Override
    public Mono<StoredUserAccount> create(UserAccountDTO draft) {
        return api.createUserAccount(draft, key()).map(dto -> new StoredUserAccount(dto.getId(), dto));
    }

    @Override
    public Mono<StoredUserAccount> update(UUID id, UserAccountDTO draft) {
        return api.updateUserAccount(id, draft, key()).map(dto -> new StoredUserAccount(dto.getId() == null ? id : dto.getId(), dto));
    }

    @Override
    public Mono<Void> remove(UUID id) {
        return api.deleteUserAccount(id, key()).then();
    }

    private static String key() {
        return UUID.randomUUID().toString();
    }
}
