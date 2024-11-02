package com.example.todoListApp.controller;

import com.example.todoListApp.model.Task;
import com.example.todoListApp.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/")
    public String home(Model model){
        List<Task> taskList = taskService.findAllTasks();
        model.addAttribute("tasks", taskList);
        return "index";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String description){
        Task task = Task.builder()
                .description(description)
                .completed(false)
                .build();
        taskService.saveTask(task);
        return "redirect:/";
    }

    @PostMapping("/complete")
    public String completedTask(@RequestParam Long id){
        taskService.completedTask(id);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteTask(@RequestParam Long id){
        taskService.deleteTask(id);
        return "redirect:/";
    }

}
