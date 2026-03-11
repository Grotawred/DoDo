package com.example.demo.repositories;

import com.example.demo.entities.CategoryType;
import com.example.demo.entities.TodoList;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> findByUser(User user);

    List<TodoList> findByUserAndCategory(User user, CategoryType category);
}