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
    @Query("SELECT t FROM ToDoItem t WHERE t.title = :title AND t.owner = :owner")
    ToDoItem findByTitle(@Param("title") String title, @Param("owner") String owner);

    @Query("SELECT t FROM ToDoItem t WHERE t.id = :id AND t.owner = :owner")
    Optional<ToDoItem> findByIdAndOwner(@Param("id") Long id, @Param("owner") String owner);

    @Query("SELECT t FROM ToDoItem t WHERE t.owner = :owner")
    Page<ToDoItem> findAll(Pageable pageable, @Param("owner") String owner);

    @Query("SELECT COUNT(t) > 0 FROM ToDoItem t WHERE t.id = :id AND t.owner = :owner") // checks if there is any record matching the given id and owner
    boolean existsByIdAndOwner(@Param("id") Long id, @Param("owner") String owner); 

    @Modifying
    @Query("DELETE FROM ToDoItem t WHERE t.owner = :owner")
    void deleteAllByOwner(@Param("owner") String owner);

    @Query("DELETE FROM ToDoItem t WHERE t.id = :id AND t.owner = :owner")
    void deleteByIdAndOwner(@Param("id") Long id, @Param("owner") String owner);
}
