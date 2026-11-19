package org.evanke.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import org.evanke.test.dto.CreateUserRequest;
import org.evanke.test.entity.User;
import org.evanke.test.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest // 啟動完整的 Spring 應用程式上下文
@AutoConfigureMockMvc // 自動設定 MockMvc，模擬 HTTP 請求
public class UserControllerUnitTest {

    @Autowired // 注入 MockMvc
    private MockMvc mockMvc;

    @MockitoBean // 用模擬物件取代 Spring 容器中的 UserService
    private UserService mockUserService;

    private User mockUser; // 測試用的單一使用者
    private List<User> mockUsers; // 測試用的使用者清單

    @BeforeEach // 每個測試方法執行前準備資料
    public void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .code("123")
                .build();

        mockUsers = Arrays.asList(
                mockUser,
                User.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").code("456").build()
        );

        Mockito.when(mockUserService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(mockUserService.getAllUsers()).thenReturn(mockUsers);
    }

    @AfterEach // 每個測試結束後重置模擬物件
    public void tearDown() {
        Mockito.reset(mockUserService);
    }

    @Test
    public void testGetUserById() throws Exception {
        User mockUser = User.builder()
                .id(1L).name("John Doe").email("john.doe@example.com").code("123").build();
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/users/1")) // 模擬 GET 請求
                .andExpect(status().isOk()) // 驗證 HTTP 200
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.code").value("123"));
    }

    @Test
    public void testCreateUserValidationError() throws Exception {
        mockMvc.perform(post("/users") // 模擬 POST 請求
                        .contentType(MediaType.APPLICATION_JSON) // 請求內容是 JSON
                        .content("{\"name\": \"Invalid User\"}")) // 刻意省略 email
                .andExpect(status().isBadRequest()) // 驗證 HTTP 400
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).name("John Doe").email("john.doe@example.com").code("123").build(),
                User.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").code("456").build()
        );
        Mockito.when(mockUserService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    public void testCreateUser() throws Exception {
        // 目前的 DTO 沒有兩參數建構子，所以使用 setter 建立請求資料。
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("John Doe");
        createUserRequest.setEmail("john.doe@example.com");
        User mockUser = User.builder()
                .id(1L).name("John Doe").email("john.doe@example.com").code("123").build();
        Mockito.when(mockUserService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new tools.jackson.databind.ObjectMapper().writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.code").value("123"));
    }
}
