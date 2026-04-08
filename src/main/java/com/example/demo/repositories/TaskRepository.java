package com.example.demo.repositories;

import com.example.demo.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTodoListIdAndCompletedFalse(Long todoListId);
    List<Task> findByTodoListIdAndCompletedTrue(Long todoListId);
}
