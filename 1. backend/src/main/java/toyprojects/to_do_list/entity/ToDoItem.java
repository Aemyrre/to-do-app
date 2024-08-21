package toyprojects.to_do_list.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
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
    
    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Column(name = "completed_at")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completedAt;

    @Transient
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;


    public ToDoItem() {
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDate.now();
        this.completedAt = null;
    }

    // All fields for testing
    public ToDoItem(Long id, String title, String description, TaskStatus status, String createdAt, String completedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDate.parse(createdAt, formatter);

        if (completedAt != null) {
            this.completedAt = LocalDate.parse(completedAt, formatter);   
        } else {
            this.completedAt = null;
        }
    }

    public ToDoItem(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDate.now();
        this.completedAt = null;
    }

    public ToDoItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDate.now();
        this.completedAt = null;
    }

    public ToDoItem(String title) {
        this.title = title;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDate.now();
        this.completedAt = null;
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

    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    // @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDate.now();
    }

    // public void setCreatedAt(String createdAt) {
    //     this.createdAt = LocalDate.parse(createdAt, formatter);
    // }

    public LocalDate getCompletedAt() {
        return this.completedAt;
    }

    public void setCompletedAt() {
        if (status == TaskStatus.PENDING) {
            this.completedAt = null;
        } else {
            this.completedAt = LocalDate.now();
        }
    }

    // public void setCompletedAt(String completedAt) {
    //     this.completedAt = LocalDate.parse(completedAt, formatter);
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
