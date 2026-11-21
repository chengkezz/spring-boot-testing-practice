# Spring Boot Testing Practice

以使用者管理 REST API 為例，練習 Spring Boot 測試，涵蓋 MockMvc、Mockito、參數化測試、H2 資料庫整合測試與 JaCoCo 覆蓋率報告。

## 使用技術

- Java 17、Spring Boot 4.1.2、Maven Wrapper
- Spring Web MVC、Spring Data JPA、Bean Validation
- H2 記憶體資料庫、Lombok
- JUnit、Mockito、MockMvc、JaCoCo

## 執行專案

需要安裝 JDK 17 或更新版本。在專案根目錄執行：

```bash
./mvnw spring-boot:run
```

應用程式預設使用 `http://localhost:8080`。H2 資料庫存在記憶體中，應用程式結束後資料不會保留。

## API

| 方法 | 路徑 | 功能 |
| --- | --- | --- |
| GET | `/users` | 查詢所有使用者 |
| GET | `/users/{id}` | 根據 ID 查詢使用者 |
| POST | `/users` | 建立使用者 |

建立使用者範例：

```bash
curl -X POST http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

姓名須為 3 至 50 個字元，Email 須符合格式。程式會檢查 Email 是否已存在。

## 執行測試

```bash
./mvnw test
```

- `UserControllerUnitTest` 使用 `@MockitoBean` 模擬 Service，透過 MockMvc 檢查 Controller 的回應與輸入驗證。
- `UserControllerIntegrationTest` 使用真正的 Service、Repository 與 H2，透過 MockMvc 測試多個層次的互動。`@Sql` 在每次測試前準備資料、測試後清除資料。
- 參數化測試分別使用 `@MethodSource` 和 `@CsvSource` 練習多組輸入。

MockMvc 不會啟動真正的 HTTP 伺服器；上述整合測試不包含網路傳輸。

## 測試覆蓋率

執行以下指令，重新編譯並產生 JaCoCo 報告：

```bash
./mvnw clean verify
```

用瀏覽器開啟 `target/jacoco-report/index.html` 即可查看。覆蓋率呈現測試執行過的程式碼，不代表所有情境都已測試。
