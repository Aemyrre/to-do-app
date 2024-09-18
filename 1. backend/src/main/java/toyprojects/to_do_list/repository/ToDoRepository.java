package toyprojects.to_do_list.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import toyprojects.to_do_list.entity.ToDoItem;

public interface ToDoRepository extends CrudRepository<ToDoItem, Long> {
    @Query("SELECT t FROM ToDoItem t WHERE t.title = :title AND t.owner = :#{authentication.name}")
    ToDoItem findByTitle(@Param("title") String title);

    @Query("SELECT t FROM ToDoItem t WHERE t.id = :id AND t.owner = :#{authentication.name}")
    Optional<ToDoItem> findByIdAndOwner(@Param("id") Long id);

    @Query("SELECT t FROM ToDoItem t WHERE t.owner = :#{authentication.name}")
    Page<ToDoItem> findAll(Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM ToDoItem t WHERE t.id = :id AND t.owner = :#{authentication.name}") // checks if there is any record matching the given id and owner
    boolean existsByIdAndOwner(@Param("id") Long id); 

    @Modifying
    @Query("DELETE FROM ToDoItem t WHERE t.owner = :#{authentication.name}")
    void deleteAllByOwner();
}
