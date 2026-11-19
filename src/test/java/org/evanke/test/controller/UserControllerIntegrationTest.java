package org.evanke.test.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest // 啟動完整的 Spring 應用上下文
@AutoConfigureMockMvc // 使用 MockMvc 模擬 HTTP 請求
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateAndGetUsers() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\": \"Test User\", \"email\": \"test@example.com\"}"))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem("Test User")))
                .andExpect(jsonPath("$[*].email").value(hasItem("test@example.com")));
    }

    @ParameterizedTest
    @MethodSource("provideCreateUserData")
    public void testCreateUserWithMethodSource(String name, String email, String errorType) throws Exception {
        var request = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"}";

        switch (errorType) {
            case "invalid":
                mockMvc.perform(post("/users").contentType("application/json").content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").value("Validation failed"));
                break;
            case "duplicate":
                mockMvc.perform(post("/users").contentType("application/json").content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Email already exists: " + email));
                break;
            case "work":
                mockMvc.perform(post("/users").contentType("application/json").content(request))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value(name))
                        .andExpect(jsonPath("$.email").value(email));
                break;
            default:
                throw new IllegalArgumentException("Invalid error type: " + errorType);
        }
    }

    private static Stream<Arguments> provideCreateUserData() {
        return Stream.of(
                Arguments.of("John", "john@example.com", "work"),
                Arguments.of("A", "short@example.com", "invalid"),
                Arguments.of("John", "invalidemail@", "invalid"),
                Arguments.of("John", "john.doe@example.com", "duplicate")
        );
    }

    @ParameterizedTest
    @CsvSource({
            "John, john@example.com, work",
            "A, short@example.com, invalid",
            "John, invalidemail@, invalid",
            "John, john.doe@example.com, duplicate"
    })
    public void testCreateUserWithCsvSource(String name, String email, String errorType) throws Exception {
        var request = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"}";

        switch (errorType) {
            case "invalid":
                mockMvc.perform(post("/users").contentType("application/json").content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").value("Validation failed"));
                break;
            case "duplicate":
                mockMvc.perform(post("/users").contentType("application/json").content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Email already exists: " + email));
                break;
            case "work":
                mockMvc.perform(post("/users").contentType("application/json").content(request))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value(name))
                        .andExpect(jsonPath("$.email").value(email));
                break;
            default:
                throw new IllegalArgumentException("Invalid error type: " + errorType);
        }
    }
}
