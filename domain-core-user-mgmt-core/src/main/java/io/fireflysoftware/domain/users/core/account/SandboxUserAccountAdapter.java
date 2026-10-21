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

import io.fireflysoftware.core.users.sdk.model.UserAccountDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Default adapter. Holds rows in memory and does not call the core. */
public final class SandboxUserAccountAdapter implements UserAccountPort {
    private final ConcurrentHashMap<UUID, UserAccountDTO> rows = new ConcurrentHashMap<>();

    @Override
    public Mono<UserAccountView> read(UUID id) {
        if (id != null && rows.containsKey(id)) {
            return Mono.just(view(id, rows.get(id)));
        }
        UUID resolved = id == null ? UUID.fromString("00000000-0000-4000-8000-000000000001") : id;
        return Mono.just(new UserAccountView(resolved, "sample@example.com"));
    }

    @Override
    public Mono<StoredUserAccount> load(UUID id) {
        UserAccountDTO body = id == null ? null : rows.get(id);
        if (body == null) {
            return Mono.error(new IllegalStateException("UserAccount " + id + " was not found"));
        }
        return Mono.just(new StoredUserAccount(id, body));
    }

    @Override
    public Mono<StoredUserAccount> create(UserAccountDTO draft) {
        if (draft == null) {
            return Mono.error(new IllegalArgumentException("UserAccount is required"));
        }
        UUID id = UUID.randomUUID();
        rows.put(id, draft);
        return Mono.just(new StoredUserAccount(id, draft));
    }

    @Override
    public Mono<StoredUserAccount> update(UUID id, UserAccountDTO draft) {
        if (id == null || !rows.containsKey(id)) {
            return Mono.error(new IllegalStateException("UserAccount " + id + " was not found"));
        }
        if (draft == null) {
            return Mono.error(new IllegalArgumentException("UserAccount is required"));
        }
        rows.put(id, draft);
        return Mono.just(new StoredUserAccount(id, draft));
    }

    @Override
    public Mono<Void> remove(UUID id) {
        if (id != null) {
            rows.remove(id);
        }
        return Mono.empty();
    }

    private static UserAccountView view(UUID id, UserAccountDTO body) {
        String label = body == null ? null : body.getEmail();
        return new UserAccountView(id, label);
    }
}
