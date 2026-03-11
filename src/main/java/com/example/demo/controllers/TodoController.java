package com.example.demo.controllers;

import com.example.demo.entities.CategoryType;
import com.example.demo.entities.Task;
import com.example.demo.entities.TodoList;
import com.example.demo.entities.User;
import com.example.demo.repositories.ListRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final ListRepository listRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String todos(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam(required = false) CategoryType category,
                        @RequestParam(required = false) Long listId,
                        Model model) {
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();

        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", category);

        List<TodoList> lists;
        if (category != null) {
            lists = listRepository.findByUserAndCategory(user, category);
        } else {
            lists = listRepository.findByUser(user);
        }

        model.addAttribute("lists", lists);

        TodoList activeList = null;
        if (listId != null) {
            activeList = listRepository.findById(listId)
                    .filter(list -> list.getUser().getId().equals(user.getId()))
                    .orElse(null);
        }

        model.addAttribute("activeList", activeList);
        return "todos";
    }

    @PostMapping("/lists")
    public String createList(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam("title") String title,
                             @RequestParam("category") CategoryType category) {
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();

        TodoList list = new TodoList();
        list.setTitle(title);
        list.setUser(user);
        list.setCategory(category);

        listRepository.save(list);
        return "redirect:/";
    }

    @PostMapping("/lists/{listId}/delete")
    public String deleteList(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long listId) {
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();

        listRepository.findById(listId)
                .filter(list -> list.getUser().getId().equals(user.getId()))
                .ifPresent(listRepository::delete);

        return "redirect:/";
    }

    @PostMapping("/tasks")
    public String addTask(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam("listId") Long listId,
                          @RequestParam("title") String title) {
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();

        TodoList list = listRepository.findById(listId)
                .filter(l -> l.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Доступ заборонено"));

        Task task = new Task();
        task.setTitle(title);
        task.setTodoList(list);

        taskRepository.save(task);
        return "redirect:/?listId=" + listId;
    }

    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long taskId,
                             @RequestParam("listId") Long listId) {
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();

        taskRepository.findById(taskId).ifPresent(task -> {
            if (task.getTodoList().getUser().getId().equals(user.getId())) {
                taskRepository.delete(task);
            }
        });

        return "redirect:/?listId=" + listId;
    }
}
