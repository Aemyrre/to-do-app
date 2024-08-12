package toyprojects.to_do_list.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import toyprojects.to_do_list.entity.ToDoItem;

public interface ToDoRepository extends CrudRepository<ToDoItem, Long> {
    ToDoItem findByTitle(String title);
    Page<ToDoItem> findAll(Pageable pageable);
}
