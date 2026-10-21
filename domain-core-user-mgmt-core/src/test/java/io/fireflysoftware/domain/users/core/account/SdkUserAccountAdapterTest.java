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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SdkUserAccountAdapterTest {
    @Test
    void sandboxIsTheDefaultAndDoesNotOpenASocket() {
        new ApplicationContextRunner()
                .withUserConfiguration(UserAccountAdapterConfiguration.class)
                .run(context -> {
                    UserAccountPort port = context.getBean(UserAccountPort.class);
                    assertThat(port).isInstanceOf(SandboxUserAccountAdapter.class);
                    assertThat(port.read(null).block().label()).isEqualTo("sample@example.com");
                });
    }

    @Test
    void corePropertySelectsTheSdkAdapter() {
        new ApplicationContextRunner()
                .withUserConfiguration(UserAccountAdapterConfiguration.class, io.fireflysoftware.domain.users.infra.UserAccountClientFactory.class)
                .withPropertyValues("firefly.user.account.adapter=core")
                .run(context -> assertThat(context.getBean(UserAccountPort.class)).isInstanceOf(SdkUserAccountAdapter.class));
    }

    @Test
    void liveReadCallsTheCoreSdk() {
        UUID id = UUID.randomUUID();
        UserAccountsApi api = mock(UserAccountsApi.class);
        UserAccountDTO dto = mock(UserAccountDTO.class);
        when(dto.getId()).thenReturn(id);
        when(api.getUserAccountById(id, null)).thenReturn(Mono.just(dto));
        UserAccountView view = new SdkUserAccountAdapter(api).read(id).block();
        assertThat(view.id()).isEqualTo(id);
        verify(api).getUserAccountById(id, null);
    }
}
