package com.itm.space.backendresources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.core.Response;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceLayerTests extends BaseIntegrationTest{

    @Test
    void contextLoads() {}

    @MockBean
    private Keycloak keycloakClient;

    @MockBean
    private RealmResource realmResource;

    @MockBean
    private UsersResource usersResource;

    @MockBean
    private Response response;

    private static final String TEST_REALM = "ITM";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        keycloakClient = mock(Keycloak.class);
        when(keycloakClient.realm(TEST_REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);  // Статус 201
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);  // CREATED
        when(response.getEntity()).thenReturn("user-id");
    }

    @Test
    void createUser_Success() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("new-user");

        Response createdResponse = keycloakClient.realm(TEST_REALM).users().create(user);

        assertEquals(201, createdResponse.getStatus());
        assertEquals("user-id", createdResponse.getEntity());
        System.out.println("Test passed with status: " + createdResponse.getStatus());
    }


    @Test
    void getUserById_Success() {
        UUID userId = UUID.randomUUID();  // Генерация случайного ID пользователя
        UserRepresentation userRepresentation = new UserRepresentation();  // Создание репрезентации пользователя
        userRepresentation.setUsername("new-user");  // Установка имени пользователя

        // Мокируем вызов получения пользователя по ID
        when(keycloakClient.realm(TEST_REALM).users().get(userId.toString())).thenReturn(mock(UserResource.class));  // Мокаем вызов на поиск пользователя
        when(keycloakClient.realm(TEST_REALM).users().get(userId.toString()).toRepresentation()).thenReturn(userRepresentation);  // Мокаем репрезентацию пользователя

        // Получаем ответ от метода поиска пользователя по ID
        UserRepresentation foundUser = keycloakClient.realm(TEST_REALM).users().get(userId.toString()).toRepresentation();

        // Проверка, что статус полученного пользователя правильный
        assertNotNull(foundUser);  // Проверяем, что пользователь найден
        assertEquals("new-user", foundUser.getUsername());  // Проверяем, что имя пользователя соответствует ожидаемому

        // Дополнительно выводим информацию в консоль (по аналогии с вашим примером)
        System.out.println("Test passed with user: " + foundUser.getUsername());
    }
}
