package toyprojects.to_do_list;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.repository.ToDoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/data.sql",executionPhase=ExecutionPhase.BEFORE_TEST_METHOD)
class ToDoListApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ToDoRepository toDoRepository;

    @Test
    void contextLoads() {
        assertNotNull(restTemplate);
    }

	@Test
	void shouldRetrieveAToDoItemWhenCalled() {
		ResponseEntity<String> response = restTemplate
			.getForEntity("/todo/101", String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		String title = documentContext.read("$.title");
		String description = documentContext.read("$.description");
		String status = documentContext.read("$.status").toString();

		assertEquals(101, id);
		assertEquals("Cook", title);
		assertEquals("Cook Adobo", description);
		assertEquals("PENDING", status);
	}

	@Test
	void shouldNotRetrieveAToDoItemUsingInvalidId() {
		ResponseEntity<String> response = restTemplate
			.getForEntity("/todo/100", String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

    @Test
    void shouldReturnAToDoItemWhenDataIsSaved() {
        ToDoItem item = new ToDoItem("Cook", "Cook Adobo");
        ToDoItem savedItem = toDoRepository.save(item);

        ResponseEntity<String> response = restTemplate
                .getForEntity("/todo/" + savedItem.getId(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String title = documentContext.read("$.title");
        String description = documentContext.read("$.description");
        String status = documentContext.read("$.status").toString();
        assertEquals(item.getTitle(), title);
        assertEquals(item.getDescription(), description);
        assertEquals(TaskStatus.PENDING.toString(), status);
    }

    @Test
    void shouldNotReturnAToDoItemWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/todo/100", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldCreateANewToDoItem() {
        ToDoItem newToDoItem = new ToDoItem(null, "Cook", "Cook Adobo");
        ResponseEntity<Void> createResponse = restTemplate
                .postForEntity("/todo", newToDoItem, Void.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        URI locationOfNewToDoItem = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(locationOfNewToDoItem, String.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        String description = documentContext.read("$.description");
        String status = documentContext.read("$.status").toString();

        assertNotNull(id);
        assertEquals(newToDoItem.getTitle(), title);
        assertEquals(newToDoItem.getDescription(), description);
        assertEquals(newToDoItem.getStatus().toString(), status);
    }

    @Test
    void shouldNotCreateANewToDoItemUsingInvalidInput() {
        ToDoItem newToDoItem = new ToDoItem(null, null, null);
        ResponseEntity<Void> createResponse = restTemplate
                .postForEntity("/todo", newToDoItem, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, createResponse.getStatusCode());
    }

    @Test
    void shouldCreateNewToDoItemWithEnumStatus() {
        String requestBody = """
				{
					"title":"Cook",
					"description":"Cook Adobo"
				}
				""";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<Void> createResponse = restTemplate
			.postForEntity("/todo", request, Void.class);
		assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

		URI locationOfNewToDoItem = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getRequest = restTemplate
			.getForEntity(locationOfNewToDoItem, String.class);
		assertEquals(HttpStatus.OK, getRequest.getStatusCode());

		DocumentContext documentContext = JsonPath.parse(getRequest.getBody());
		System.out.println("print body: " + getRequest.getBody());
		Number id = documentContext.read("$.id");
		String title = documentContext.read("$.title");
		String description = documentContext.read("$.description");
		String status = documentContext.read("$.status");
		assertNotNull(id);
		assertEquals("Cook", title);
		assertEquals("Cook Adobo", description);
		assertEquals(TaskStatus.PENDING.toString(), status);
    }

	@Test
	void shouldUpdatePendingStatusToCompleted() {
		ResponseEntity<Void> putResponse = restTemplate
			.exchange("/todo/101/statusUpdate", HttpMethod.PUT, null, Void.class);
		assertEquals(HttpStatus.OK, putResponse.getStatusCode());

		ResponseEntity<String> getResponse = restTemplate
			.getForEntity("/todo/101", String.class);
		assertEquals(HttpStatus.OK, getResponse.getStatusCode());

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		String status = documentContext.read("$.status").toString();
		assertEquals("COMPLETED", status);
	}

	@Test
	void shouldUpdateCompletedStatusToPending() {
		ResponseEntity<Void> putResponse = restTemplate
			.exchange("/todo/102/statusUpdate", HttpMethod.PUT, null, Void.class);
		assertEquals(HttpStatus.OK, putResponse.getStatusCode());

		ResponseEntity<String> getResponse = restTemplate
			.getForEntity("/todo/102", String.class);
		assertEquals(HttpStatus.OK, getResponse.getStatusCode());

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		String status = documentContext.read("$.status").toString();
		assertEquals("PENDING", status);
	}
}
