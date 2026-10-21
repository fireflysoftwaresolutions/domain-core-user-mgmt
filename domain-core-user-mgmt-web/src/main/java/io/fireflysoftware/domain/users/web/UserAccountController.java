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

package io.fireflysoftware.domain.users.web;

import io.fireflysoftware.core.users.sdk.model.UserAccountDTO;
import io.fireflysoftware.domain.users.core.account.UserAccountService;
import io.fireflysoftware.domain.users.core.account.UserAccountView;
import io.fireflysoftware.domain.users.core.account.StoredUserAccount;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class UserAccountController {
    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mono<UserAccountView> read(@PathVariable UUID id) {
        return service.read(id);
    }

    @PostMapping
    public Mono<StoredUserAccount> create(@RequestBody UserAccountDTO draft) {
        return service.register(draft);
    }

    @PutMapping("/{id}")
    public Mono<StoredUserAccount> update(@PathVariable UUID id, @RequestBody UserAccountDTO draft) {
        return service.update(id, draft);
    }
}
