package toyprojects.to_do_list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;
import toyprojects.to_do_list.repository.ToDoRepository;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

	
}
