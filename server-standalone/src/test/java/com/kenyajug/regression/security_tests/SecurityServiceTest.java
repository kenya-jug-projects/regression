package com.kenyajug.regression.security_tests;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.security.SecurityService;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SecurityService securityService;
    @Test
    @DisplayName("Should load SecurityUser by username")
    public void shouldLoadSecurityUserByUsernameTest(){
        var userEntity = new User(
                "UUID1",
                "gritmatch453@gmail.com",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        when(userRepository.findByUsername(userEntity.username()))
                .thenReturn(Optional.of(userEntity));
        var userDetails = securityService.loadUserByUsername(userEntity.username());
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(userEntity.username());
        assertThat(userDetails.getPassword()).isEqualTo(userEntity.password());
    }
    @Test
    @DisplayName("Should throw SecurityException when security conditions are violated")
    public void shouldThrowSecurityExceptionTest(){
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> securityService.loadUserByUsername(anyString()))
                .hasMessage("Unauthorized access, invalid credentials");
    }
}
