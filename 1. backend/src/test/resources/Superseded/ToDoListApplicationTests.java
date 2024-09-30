// package toyprojects.to_do_list;

// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.jdbc.Sql;
// import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

// import toyprojects.to_do_list.repository.ToDoRepository;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// @Sql(scripts = "/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
// @Import(TestSecurityConfig.class)
// @WithMockJwt(subject = "Ramyr", scope = "SCOPE_todo:read SCOPE_todo:write SCOPE_todo:update SCOPE_todo:delete")
// class ToDoListApplicationTests {

//     @Autowired
//     TestRestTemplate restTemplate;

//     @Autowired
//     ToDoRepository toDoRepository;

//     @Value("${auth.jwt:default-value}")
//     String jwt;

//     @Test
//     void contextLoads() {
//         assertNotNull(restTemplate);
//         assertNotNull(toDoRepository);
//     }

    // @Test
    // void shouldRetrieveAToDoItemWhenCalled() {
    //     ResponseEntity<String> response = restTemplate
    //             .getForEntity("/todo/101", String.class);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());

        // DocumentContext documentContext = JsonPath.parse(response.getBody());
        // Number id = documentContext.read("$.id");
        // String title = documentContext.read("$.title");
        // String description = documentContext.read("$.description");
        // String status = documentContext.read("$.status").toString();
        // String createdAt = documentContext.read("$.createdAt");
        // String completeddAt = documentContext.read("$.completedAt");
        // String owner = documentContext.read("$.owner");

        // assertEquals(101, id);
        // assertEquals("Cook", title);
        // assertEquals("Cook Adobo", description);
        // assertEquals("PENDING", status);
        // assertEquals(LocalDate.now().toString(), createdAt);
        // assertNull(completeddAt);
        // assertEquals("Ramyr", owner);
    // }

//     @Test
//     void shouldNotRetrieveAToDoItemUsingInvalidId() {
//         ResponseEntity<String> response = restTemplate
//                 .getForEntity("/todo/100", String.class);
//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

//         DocumentContext documentContext = JsonPath.parse(response.getBody());
//         String error = documentContext.read("$.error");
//         String message = documentContext.read("$.message");

//         assertEquals("Not Found", error);
//         assertEquals("Id 100 Not Found", message);
//     }

//     @Test
//     void shouldNotReturnAToDoItemWithAnUnknownId() {
//         ResponseEntity<String> response = restTemplate
//                 .getForEntity("/todo/100", String.class);
//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//     }

    // @Test
    // void shouldCreateANewToDoItem() throws UnirestException {


    //     HttpResponse<String> unirestResponse = Unirest.get("http://localhost:8080")
    //         .header("authorization", "Bearer " + jwt)
    //         .asString();

    //     String token = unirestResponse.getBody();

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.setBearerAuth(token);

    //     String requestBody = """
	// 			{
	// 				"title":"Cook",
	// 				"description":"Cook Adobo"
	// 			}
	// 			""";
        
    //     HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
    //     ResponseEntity<Void> createResponse = restTemplate
    //             .exchange(
    //                 "/todo", 
    //                 HttpMethod.POST, entity, 
    //                 Void.class);
    //     assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

    //     URI locationOfNewToDoItem = createResponse.getHeaders().getLocation();
    //     ResponseEntity<String> getResponse = restTemplate
    //             .getForEntity(locationOfNewToDoItem, String.class);
    //     assertEquals(HttpStatus.OK, getResponse.getStatusCode());

    //     DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
    //     Number id = documentContext.read("$.id");
    //     String title = documentContext.read("$.title");
    //     String description = documentContext.read("$.description");
    //     String status = documentContext.read("$.status").toString();
    //     String createdAt = documentContext.read("$.createdAt");
    //     String completedAt = documentContext.read("$.completedAt");

    //     assertNotNull(id);
    //     assertEquals(newToDoItem.getTitle(), title);
    //     assertEquals(newToDoItem.getDescription(), description);
    //     assertEquals(newToDoItem.getStatus().toString(), status);
    //     assertEquals(LocalDate.now().toString(), createdAt);
    //     assertNull(completedAt);
    // }

    // @Test
    // void shouldNotCreateANewToDoItemUsingInvalidInput() {
    //     ToDoItem newToDoItem = new ToDoItem(null, null, null);
    //     ResponseEntity<Void> createResponse = restTemplate
    //             .postForEntity("/todo", newToDoItem, Void.class);
    //     assertEquals(HttpStatus.BAD_REQUEST, createResponse.getStatusCode());
    // }

//     @Test
//     void shouldCreateNewToDoItemWithEnumStatus() {
//         String requestBody = """
// 				{
// 					"title":"Cook",
// 					"description":"Cook Adobo"
// 				}
// 				""";

//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Content-Type", "application/json");
//         HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

//         ResponseEntity<Void> createResponse = restTemplate
//                 .postForEntity("/todo", request, Void.class);
//         assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

//         URI locationOfNewToDoItem = createResponse.getHeaders().getLocation();
//         ResponseEntity<String> getRequest = restTemplate
//                 .getForEntity(locationOfNewToDoItem, String.class);
//         assertEquals(HttpStatus.OK, getRequest.getStatusCode());

//         DocumentContext documentContext = JsonPath.parse(getRequest.getBody());
//         System.out.println("print body: " + getRequest.getBody());
//         Number id = documentContext.read("$.id");
//         String title = documentContext.read("$.title");
//         String description = documentContext.read("$.description");
//         String status = documentContext.read("$.status");
//         String createdAt = documentContext.read("$.createdAt");
//         String completedAt = documentContext.read("$.completedAt");

//         assertNotNull(id);
//         assertEquals("Cook", title);
//         assertEquals("Cook Adobo", description);
//         assertEquals(TaskStatus.PENDING.toString(), status);
//         assertEquals(LocalDate.now().toString(), createdAt);
//         assertNull(completedAt);
//     }

//     @Test
//     void shouldUpdatePendingStatusToCompleted() {
//         ResponseEntity<Void> putResponse = restTemplate
//                 .exchange("/todo/101/statusUpdate", HttpMethod.PUT, null, Void.class);
//         assertEquals(HttpStatus.OK, putResponse.getStatusCode());

//         ResponseEntity<String> getResponse = restTemplate
//                 .getForEntity("/todo/101", String.class);
//         assertEquals(HttpStatus.OK, getResponse.getStatusCode());

//         DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
//         String status = documentContext.read("$.status").toString();
//         String completedAt = documentContext.read("$.completedAt");

//         assertEquals("COMPLETED", status);
//         assertEquals(LocalDate.now().toString(), completedAt);        
//     }

//     @Test
//     void shouldUpdateCompletedStatusToPending() {
//         ResponseEntity<Void> putResponse = restTemplate
//                 .exchange("/todo/102/statusUpdate", HttpMethod.PUT, null, Void.class);
//         assertEquals(HttpStatus.OK, putResponse.getStatusCode());

//         ResponseEntity<String> getResponse = restTemplate
//                 .getForEntity("/todo/102", String.class);
//         assertEquals(HttpStatus.OK, getResponse.getStatusCode());

//         DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
//         String status = documentContext.read("$.status").toString();
//         String completedAt = documentContext.read("$.completedAt");

//         assertEquals("PENDING", status);
//         assertNull(completedAt);
//     }

//     @Test
//     void shouldDeleteAnExistingToDoItem() {
//         ResponseEntity<Void> deleteResponse = restTemplate
//                 .exchange("/todo/101", HttpMethod.DELETE, null, Void.class);
//         assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
//     }

//     @Test
//     void shouldThrowAnErrorWhenDeletingANonExistingId() {
//         ResponseEntity<String> deleteResponse = restTemplate
//                 .exchange("/todo/10000", HttpMethod.DELETE, null, String.class);
//         assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());

//         DocumentContext documentContext = JsonPath.parse(deleteResponse.getBody());
//         String error = documentContext.read("$.error");
//         String message = documentContext.read("$.message");

//         assertEquals("Not Found", error);
//         assertEquals("Id 10000 Not Found", message);
//     }

//     @Test
//     void shouldReturnAllToDoListItems() {
//         ResponseEntity<String> response = restTemplate
//                 .getForEntity("/todo/all", String.class);
//         assertEquals(HttpStatus.OK, response.getStatusCode());

//         System.out.println("this is the response body: " + response.getBody());
//         DocumentContext documentContext = JsonPath.parse(response.getBody());
//         int numberOfElements = documentContext.read("$.numberOfElements");
//         JSONArray ids = documentContext.read("$..id");
//         JSONArray titles = documentContext.read("$..title");

//         assertEquals(3, numberOfElements);
//         assertThat(ids).containsExactly(101, 102, 103);
//         assertThat(titles).containsExactly("Cook", "Exercise", "Read");
//     }

//     @Test
//     void shouldReturnSortedAndPaginatedToDoListItems() {
//         ResponseEntity<String> response = restTemplate
//                 .getForEntity("/todo/all?page=2&size=1&sort=title", String.class);
//         assertEquals(HttpStatus.OK, response.getStatusCode());

//         System.out.println("this is the response body: " + response.getBody());

//         DocumentContext documentContext = JsonPath.parse(response.getBody());
//         JSONArray ids = documentContext.read("$..id");
//         JSONArray titles = documentContext.read("$..title");
//         int numberOfElements = documentContext.read("$.numberOfElements");

//         assertEquals(1, numberOfElements);
//         assertThat(ids).containsExactly(103);
//         assertThat(titles).containsExactly("Read");
//     }

//     @Test
//     void shouldUpdateAValidToDoItem() {
//         ToDoItem updateDoItem = new ToDoItem(null, "Read", "Read HeadFirst Java");
//         HttpEntity<ToDoItem> newToDoitem = new HttpEntity<>(updateDoItem);
//         ResponseEntity<Void> putResponse = restTemplate
//                 .exchange("/todo/103", HttpMethod.PUT, newToDoitem, Void.class);
//         assertEquals(HttpStatus.OK, putResponse.getStatusCode());

//         ResponseEntity<String> getResponse = restTemplate
//                 .getForEntity("/todo/103", String.class);
//         assertEquals(HttpStatus.OK, getResponse.getStatusCode());

//         DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
//         String title = documentContext.read("$.title");
//         String description = documentContext.read("$.description");

//         assertEquals(updateDoItem.getTitle(), title);
//         assertEquals(updateDoItem.getDescription(), description);
//     }

//     @Test
//     void shouldNotUpdateWithAnInvalidToDoItem() {
//         ToDoItem updaToDoItem = new ToDoItem(null, " ", "Read HeadFirst Java");
//         HttpEntity<ToDoItem> newToDoItem = new HttpEntity<>(updaToDoItem);

//         ResponseEntity<Void> putResponse = restTemplate
//                 .exchange("/todo/103", HttpMethod.PUT, newToDoItem, Void.class);
//         assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
//     }

//     @Test
//     void shouldNotUpdateWithAnInvalidId() {
//         ToDoItem updateDoItem = new ToDoItem(null, "Read", "Read HeadFirst Java");
//         HttpEntity<ToDoItem> httpEntity = new HttpEntity<>(updateDoItem);
//         ResponseEntity<Void> putResponse = restTemplate
//                 .exchange("/todo/1000", HttpMethod.PUT, httpEntity, Void.class);
//         assertEquals(HttpStatus.NOT_FOUND, putResponse.getStatusCode());
//     }

//     @Test
//     void shouldNotUpdateWithAnInvalidToDoItemId() {
//         ToDoItem updateDoItem = new ToDoItem(100L, "Read", "Read HeadFirst Java");
//         HttpEntity<ToDoItem> httpEntity = new HttpEntity<>(updateDoItem);
//         ResponseEntity<Void> putResponse = restTemplate
//                 .exchange("/todo/103", HttpMethod.PUT, httpEntity, Void.class);
//         assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
//     }

//     @Test
//     void shouldSaveAllToDoItems() {
//         String requestBody = """
// 				[
// 					{
// 						"title":"Cook",
// 						"description":"Cook Adobo"
// 					},
// 					{
// 						"title":"Read",
// 						"description":"Read Book"
// 					},
// 					{
// 						"title":"Wash",
// 						"description":"Wash Clothes"
// 					}
// 				]
// 				""";

//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Content-Type", "application/json");
//         HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

// 		ResponseEntity<Void> postRequest = restTemplate
// 			.postForEntity("/todo/saveAll", httpEntity, Void.class);
// 		assertEquals(HttpStatus.OK, postRequest.getStatusCode());

// 		ResponseEntity<String> getRequest = restTemplate
// 			.getForEntity("/todo/all", String.class);
// 		assertEquals(HttpStatus.OK, getRequest.getStatusCode());

// 		DocumentContext documentContext = JsonPath.parse(getRequest.getBody());
// 		JSONArray ids = documentContext.read("$..id");
// 		JSONArray titles = documentContext.read("$..title");
// 		assertThat(ids).containsExactly(1, 2 ,3, 101, 102, 103);
// 		assertThat(titles).containsExactlyInAnyOrder("Cook", "Read", "Wash", "Cook", "Exercise", "Read");
//     }

// 	@Test
// 	void shouldNotSaveToDoitemsWithInvaidInput() {
// 		String requestBody = """
// 				[
// 					{
// 						"title":"Cook",
// 						"description":"Cook Adobo"
// 					},
// 					{
// 						"title":null,
// 						"description":"Read Book"
// 					},
// 					{
// 						"title":"Wash",
// 						"description":"Wash Clothes"
// 					}
// 				]
// 				""";

// 		HttpHeaders header = new HttpHeaders();
// 		header.set("Content-Type", "application/json");
// 		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, header);

// 		ResponseEntity<Void> postResponse = restTemplate
// 			.postForEntity("/todo/saveAll", httpEntity, Void.class);
// 		assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
// 	}

// 	@Test
// 	void shouldDeleteAllToDoItems() {
// 		assertEquals(3, toDoRepository.count());
// 		ResponseEntity<Void> response = restTemplate
// 			.exchange("/todo/all", HttpMethod.DELETE, null, Void.class);
// 		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

// 		ResponseEntity<String> getResponse = restTemplate
// 			.getForEntity("/todo/all", String.class);
// 		assertEquals(HttpStatus.OK, getResponse.getStatusCode());

// 		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
// 		JSONArray ids = documentContext.read("$..id");
// 		JSONArray titles = documentContext.read("$..title");
		
// 		assertThat(ids).isEmpty();
// 		assertThat(titles).isEmpty();
// 	}
// }
