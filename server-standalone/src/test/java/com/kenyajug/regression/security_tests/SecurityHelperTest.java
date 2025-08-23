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
 *
 */
import com.kenyajug.regression.security.SecurityHelper;
import com.kenyajug.regression.security.SecurityUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class SecurityHelperTest {
    private SecurityHelper securityHelper;
    @BeforeEach
    void setUp() {
        securityHelper = new SecurityHelper();
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    @Test
    @DisplayName("Should return SecurityUser when the principal is valid")
    void findAuthenticatedUser_returnsSecurityUser_whenPrincipalIsValid() {
        var mockUser = new SecurityUser("username", "password", List.of()); // adjust constructor as needed
        var authentication = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityUser result = securityHelper.findAuthenticatedUser();
        assertNotNull(result);
        assertEquals(mockUser, result);
    }
    @Test
    @DisplayName("Should throw SecurityException when principal is not a SecurityUser")
    void findAuthenticatedUser_throwsException_whenPrincipalIsNotSecurityUser() {
        var authentication = new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityException exception = assertThrows(SecurityException.class, () ->
                securityHelper.findAuthenticatedUser());
        assertEquals("Could not determine authenticated user", exception.getMessage());
    }
    @Test
    @DisplayName("Should throw SecurityException when authentication is null")
    void findAuthenticatedUser_throwsException_whenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();
        assertThrows(NullPointerException.class, () ->
                securityHelper.findAuthenticatedUser());
    }
    @Test
    @DisplayName("Should clear the SecurityContext on logout")
    void forceLogout_clearsSecurityContext() {
        var mockUser = new SecurityUser("user", "pass", List.of());
        var auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        securityHelper.forceLogout();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

