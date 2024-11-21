package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser", roles = "MODERATOR") // Устанавливаем пользователя и роль для тестов
public class ApiLayerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testUser", roles = "MODERATOR")
    public void testCreateUser() throws Exception {
        // Создаем запрос для создания пользователя
        UserRequest userRequest;
        userRequest = new UserRequest("testUsername", "testEmail@gmail.com", "testPassword",
                                      "TestName", "TestLastName");

        String userJson = objectMapper.writeValueAsString(userRequest);

        // Мокаем сервис
        doNothing().when(userService).createUser(any(UserRequest.class));

        // Выполняем POST запрос для создания пользователя
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk()); // Ожидаем успешного статуса

        // Проверяем, был ли вызван метод createUser()
        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "MODERATOR")
    public void testGetUserById() throws Exception {
        // Создаем ID пользователя
        UUID userId = UUID.randomUUID();

        // Создаем ответ от сервиса
        UserResponse userResponse = new UserResponse("testUsername", "testEmail@gmail.com", "testPassword", null, null);

        // Мокаем метод getUserById()
        when(userService.getUserById(userId)).thenReturn(userResponse);

        // Отправляем GET запрос для получения пользователя по ID
        mockMvc.perform(get("/api/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Ожидание статуса 200
                .andExpect(jsonPath("firstName").value("testUsername")); // Проверка соответствия
                                                                                        // в ответе имени пользователя

        // Проверяем, был ли вызван метод getUserById()
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @WithMockUser(username = "testUser", roles = "MODERATOR")
    public void testHello() throws Exception {
        // Отправляем GET запрос на эндпоинт hello
        mockMvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk()) // Ожидаем статус 200
                .andExpect(content().string("testUser")); // Проверка соответствия в ответе
                                                                        // имени пользователя значению "testUser"
    }
}
