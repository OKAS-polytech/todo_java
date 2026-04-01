package com.example.todo.application.services;

import com.example.todo.application.ports.out.UserRepositoryPort;
import com.example.todo.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepositoryPort userRepo;
    private AuthService authService;

    @BeforeEach void setUp() { authService = new AuthService(userRepo); }

    @Test
    @DisplayName("正常系：ユーザー登録ができること")
    void testRegister() {
        when(userRepo.findByUsername("test")).thenReturn(null);
        User user = authService.register("test", "password");
        assertNotNull(user);
        assertEquals("test", user.getUsername());
        verify(userRepo).save(any(User.class));
    }

    @Test
    @DisplayName("正常系：ログインができること")
    void testLogin() {
        String pass = "password";
        String hash = Integer.toHexString(pass.hashCode());
        User mockUser = new User(1L, "user1", hash);
        when(userRepo.findByUsername("user1")).thenReturn(mockUser);

        User loggedIn = authService.login("user1", pass);
        assertEquals(1L, loggedIn.getId());
    }
}
