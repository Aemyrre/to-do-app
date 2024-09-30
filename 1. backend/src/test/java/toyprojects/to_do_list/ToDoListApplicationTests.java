package toyprojects.to_do_list;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItemRequest;
import toyprojects.to_do_list.repository.ToDoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=3000")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
class ToDoListApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ToDoRepository toDoRepository;

    @Value("${auth.jwt:default-value}")
    String jwt;

    @Test
    void contextLoads() {
        assertNotNull(restTemplate);
        assertNotNull(toDoRepository);
    }

    @Test
    void shouldCreateValidToDoItemAndFetchDataForValidation() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read book");
        String location = createValidToDoObject(todo);

        mockMvc.perform(get(location)
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.title").value(todo.getTitle()))
            .andExpect(jsonPath("$.description").value(todo.getDescription()))
            .andExpect(jsonPath("$.status").value(TaskStatus.PENDING.toString()))
            .andExpect(jsonPath("$.createdAt").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.completedAt").isEmpty())
            .andExpect(jsonPath("$.owner").isNotEmpty());
    }

    @Test
    void shouldNotCreateToDoItemWithInvalidData() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest(" ", " ");
        String jsonRequest = new ObjectMapper().writeValueAsString(todo);
 
        mockMvc.perform(post("/todo")
            .header("Authorization", "Bearer "+ jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void shouldNotFetchDataOfAnotherUser() throws Exception {
        mockMvc.perform(get("/todo/101")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Id Not Found"));
    }

    @Test
    void shouldNotFetchInvalidData() throws Exception {
        mockMvc.perform(get("/todo/10")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Id Not Found"));
    }

    @Test
    void shouldUpdatePendingStatusToCompleted() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");

        String location = createValidToDoObject(todo);
        checkToDoStatus(location, "PENDING");
        
        mockMvc.perform(put(location + "/statusUpdate")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk());
        checkToDoStatus(location, "COMPLETED");
    }

    @Test
    void shouldRevertCompletedStatusToPending() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");
        String location = createValidToDoObject(todo);
        checkToDoStatus(location, "PENDING");
        
        mockMvc.perform(put(location + "/statusUpdate")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk());
        checkToDoStatus(location, "COMPLETED");

        mockMvc.perform(put(location + "/statusUpdate")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk());
        checkToDoStatus(location, "PENDING");
    }

    @Test
    void shouldUpdateAValidToDoItem() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");
        String location = createValidToDoObject(todo);

        mockMvc.perform(get(location)
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.title").value(todo.getTitle()),
                jsonPath("$.description").value(todo.getDescription())
            );    
        
        ToDoItemRequest putTodo = new ToDoItemRequest("Sleep", "Rest after reading book");
        String jsonString = new ObjectMapper().writeValueAsString(putTodo);
        updateToDoItem(location, jsonString);

        mockMvc.perform(get(location)
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.title").value(putTodo.getTitle()),
                jsonPath("$.description").value(putTodo.getDescription())
            );
    }

    @Test
    void shouldNotUpdateToDoItemFromAnotherUser() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");
        String jsonString = new ObjectMapper().writeValueAsString(todo);
        
        mockMvc.perform(put("/todo/101")
            .header("Authorization", "Bearer " + jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Id Not Found"));
    }

    @Test
    void shouldNotUpdateInvalidId() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");
        String jsonString = new ObjectMapper().writeValueAsString(todo);
        
        mockMvc.perform(put("/todo/1010")
            .header("Authorization", "Bearer " + jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Id Not Found"));
    }

    @Test
    void shouldNotUpdateWithInvalidInput() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");
        String location = createValidToDoObject(todo);
        
        ToDoItemRequest putTodo = new ToDoItemRequest(null, "Read Book");
        String invalidToDo = new ObjectMapper().writeValueAsString(putTodo);
        mockMvc.perform(put(location)
            .header("Authorization", "Bearer " + jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidToDo)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Title cannot be null or empty"));
    }

    @Test
    void shouldDeleteAnExistingToDoItem() throws Exception {
        ToDoItemRequest todo = new ToDoItemRequest("Read", "Read Book");
    
        String location = createValidToDoObject(todo);
        mockMvc.perform(delete(location)
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteToDoItemofAnotherUser() throws Exception {
        mockMvc.perform(delete("/todo/101")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Id Not Found"));
    }

    @Test
    void shouldNotDeleteWithAnInvalidId() throws Exception {
        mockMvc.perform(delete("/todo/1010")
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Id Not Found"));
    }

    private String createValidToDoObject(ToDoItemRequest todo) throws Exception {
        String jsonRequest = new ObjectMapper().writeValueAsString(todo);

        String location = mockMvc.perform(post("/todo")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("location");
        
        return location;
    }
    private void checkToDoStatus(String location, String expectedStatus) throws Exception {
        mockMvc.perform(get(location)
            .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(expectedStatus));
    }

    private void updateToDoItem(String location, String jsonString) throws Exception {
        mockMvc.perform(put(location)
        .header("Authorization", "Bearer " + jwt)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonString)
        )
        .andExpect(status().isOk());
    }
}
