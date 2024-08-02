package toyprojects.to_do_list.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import toyprojects.to_do_list.constants.TaskStatus;


@Entity
@Table(name = "to_do")
public class ToDoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "title cannot be blank")
    private String title;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    // @Column(name = "createdAt")
    // @Temporal(TemporalType.DATE)    
    // private LocalDateTime createdAt;

    // @Column(name = "completedAt")
    // @Temporal(TemporalType.DATE)
    // private LocalDateTime completedAt;


    public ToDoItem() {
    }

    public ToDoItem(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
    }

    public ToDoItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
    }

    public ToDoItem(String title) {
        this.title = title;
        this.status = TaskStatus.PENDING;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    // public LocalDateTime getCreatedAt() {
    //     return this.createdAt;
    // }

    // @PrePersist
    // public void setCreatedAt() {
    //     this.createdAt = LocalDateTime.now();
    // }

    // public LocalDateTime getCompletedAt() {
    //     return this.completedAt;
    // }

    // public void setCompletedAt() {
    //     this.completedAt = LocalDateTime.now();
    // }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ToDoItem)) {
            return false;
        }
        ToDoItem toDoItem = (ToDoItem) o;
        return Objects.equals(id, toDoItem.id) && Objects.equals(title, toDoItem.title) && Objects.equals(description, toDoItem.description) && Objects.equals(status, toDoItem.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

}
