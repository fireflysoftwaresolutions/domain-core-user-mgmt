/*
 * Copyright 2025 Firefly Software Foundation
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

package io.fireflysoftware.domain.users;

import io.fireflysoftware.core.users.sdk.api.UserAccountsApi;
import io.fireflysoftware.core.users.sdk.invoker.ApiClient;
import io.fireflysoftware.core.users.sdk.model.UserAccountDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/** Live adapter. Calls core-common-user-mgmt-sdk. Selected with firefly.user.account.adapter=core. */
public final class SdkAdapter implements CorePort {
    private final UserAccountsApi api;

    public SdkAdapter(String basePath) {
        ApiClient client = new ApiClient();
        client.setBasePath(basePath);
        this.api = new UserAccountsApi(client);
    }

    SdkAdapter(UserAccountsApi api) {
        this.api = api;
    }

    @Override
    public Mono<UserAccountView> read(UUID id) {
        return api.getUserAccountById(id, null).map(dto -> new UserAccountView(dto.getId(), dto.getEmail()));
    }
}
