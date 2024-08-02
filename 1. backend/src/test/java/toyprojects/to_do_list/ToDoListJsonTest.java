package toyprojects.to_do_list;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import toyprojects.to_do_list.constants.TaskStatus;
import toyprojects.to_do_list.entity.ToDoItem;

@JsonTest
public class ToDoListJsonTest {

    @Autowired
    JacksonTester<ToDoItem> json;

    @Autowired
    JacksonTester<ToDoItem[]> jsonList;

    ToDoItem[] toDoItems;

    @BeforeEach
    void setUp() {
        toDoItems = Arrays.array(
                new ToDoItem(101L, "Cook", "Cook Adobo"),
                new ToDoItem(102L, "Eat", "Eat Adobo"),
                new ToDoItem(103L, "Wash", "Wash Dishes"));
    }

    @Test
    void toDoSerializationJsonTest() throws IOException {
        ToDoItem toDo = new ToDoItem(1L, "Breakfast", "Drink Coffee and Eat Bread");

        assertThat(json.write(toDo)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(toDo)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(toDo)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.title");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.title").isEqualTo(toDo.getTitle());
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.description");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.description").isEqualTo(toDo.getDescription());
        assertThat(json.write(toDo)).hasJsonPathStringValue("@.status");
        assertThat(json.write(toDo)).extractingJsonPathStringValue("@.status").isEqualTo("PENDING");
    }

    @Test
    void toDoDeserializationJsonTest() throws IOException {
        String expected = """
                {
                    "id":1,
                    "title":"Breakfast",
                    "description":"Drink Coffee and Eat Bread",
                    "status":"PENDING"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new ToDoItem(1L, "Breakfast", "Drink Coffee and Eat Bread"));
        assertThat(json.parseObject(expected).getId()).isEqualTo(1);
        assertThat(json.parseObject(expected).getTitle()).isEqualTo("Breakfast");
        assertThat(json.parseObject(expected).getDescription()).isEqualTo("Drink Coffee and Eat Bread");
        assertThat(json.parseObject(expected).getStatus()).isEqualTo(TaskStatus.PENDING);
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
                        "status": "PENDING"
                    },
                    {
                        "id": 102,
                        "title": "Eat",
                        "description": "Eat Adobo",
                        "status": "PENDING"
                    },
                    {
                        "id": 103,
                        "title": "Wash",
                        "description": "Wash Dishes",
                        "status": "PENDING"
                    }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(toDoItems);
    }
}
