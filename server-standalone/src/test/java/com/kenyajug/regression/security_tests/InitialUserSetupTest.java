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
import com.kenyajug.regression.InitialUserSetup;
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
public class InitialUserSetupTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private InitialUserSetup initialUserSetup;
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        initialUserSetup = new InitialUserSetup();
    }
    @DisplayName("Should create a user if one does not already exist")
    @Test
    void shouldCreateUserIfNotExists() throws Exception {
        var username = "admin@regression.com";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("hashedPassword");
        CommandLineRunner runner = initialUserSetup.createDefaultUser(userRepository, passwordEncoder);
        runner.run();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.username()).isEqualTo(username);
        assertThat(savedUser.password()).isEqualTo("hashedPassword");
        assertThat(savedUser.roles_list_json()).contains("{ROLE_ADMIN","ROLE_USER}");
    }
    @DisplayName("Should not create a user if one already exists")
    @Test
    void shouldNotCreateUserIfAlreadyExists() throws Exception {
        when(userRepository.existsByUsername("admin@regression.com")).thenReturn(true);
        CommandLineRunner runner = initialUserSetup.createDefaultUser(userRepository, passwordEncoder);
        runner.run();
        verify(userRepository, never()).save(any());
    }
}
