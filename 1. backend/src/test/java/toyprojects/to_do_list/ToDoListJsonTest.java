package toyprojects.to_do_list;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.security.test.context.support.WithMockUser;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;

@JsonTest
@WithMockUser(username="Ramyr")
public class ToDoListJsonTest {

    @Autowired
    JacksonTester<ToDoItem> json;

    @Autowired
    JacksonTester<ToDoItem[]> jsonList;

    ToDoItem[] toDoItems;

    @BeforeEach
    void setUp() {
        toDoItems = Arrays.array(
                new ToDoItem(101L, "Cook", "Cook Adobo", TaskStatus.COMPLETED, "2024-08-20", "2024-08-21", "Ramyr"),
                new ToDoItem(102L, "Eat", "Eat Adobo", TaskStatus.PENDING, "2024-08-20", null, "Ramyr"),
                new ToDoItem(103L, "Wash", "Wash Dishes", TaskStatus.PENDING, "2024-08-20", null, "Ramyr"));
    }

    @Test
    void toDoSerializationJsonTest() throws IOException {
        ToDoItem toDo = new ToDoItem(1L, "Breakfast", "Drink Coffee and Eat Bread", TaskStatus.PENDING, "2024-08-20", null, "Ramyr");
        
        assertThat(json.write(toDo)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(toDo)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(toDo)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.title");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.title").isEqualTo(toDo.getTitle());
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.description");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.description").isEqualTo(toDo.getDescription());
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.status");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.status").isEqualTo("PENDING");
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.createdAt").isEqualTo("2024-08-20");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.completedAt").isNull();
    }

    @Test
    void toDoDeserializationJsonTest() throws IOException {
        String expected = """
                {
                    "id":1,
                    "title":"Breakfast",
                    "description":"Drink Coffee and Eat Bread",
                    "status":"COMPLETED",
                    "createdAt":"2024-08-20",
                    "completedAt":"2024-08-21",
                    "owner":"Ramyr"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new ToDoItem(1L, "Breakfast", "Drink Coffee and Eat Bread", TaskStatus.COMPLETED, "2024-08-20", "2024-08-21", "Ramyr"));
        assertThat(json.parseObject(expected).getId()).isEqualTo(1);
        assertThat(json.parseObject(expected).getTitle()).isEqualTo("Breakfast");
        assertThat(json.parseObject(expected).getDescription()).isEqualTo("Drink Coffee and Eat Bread");
        assertThat(json.parseObject(expected).getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(json.parseObject(expected).getCreatedAt()).isEqualTo("2024-08-20");
        assertThat(json.parseObject(expected).getCompletedAt()).isEqualTo("2024-08-21");
    }

    @Test
    void toDoListSerializationJsonTest() throws IOException {
        assertThat(jsonList.write(toDoItems)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void toDoListDeserializationJsonTest() throws IOException {
        String expected = """
                [
                    {
                        "id": 101,
                        "title": "Cook",
                        "description": "Cook Adobo",
                        "status": "COMPLETED",
                        "createdAt":"2024-08-20",
                        "completedAt":"2024-08-21",
                        "owner":"Ramyr"
                    },
                    {
                        "id": 102,
                        "title": "Eat",
                        "description": "Eat Adobo",
                        "status": "PENDING",
                        "createdAt":"2024-08-20",
                        "completedAt":null,
                        "owner":"Ramyr"
                    },
                    {
                        "id": 103,
                        "title": "Wash",
                        "description": "Wash Dishes",
                        "status": "PENDING",
                        "createdAt":"2024-08-20",
                        "completedAt":null,
                        "owner":"Ramyr"
                    }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(toDoItems);
    }
}
