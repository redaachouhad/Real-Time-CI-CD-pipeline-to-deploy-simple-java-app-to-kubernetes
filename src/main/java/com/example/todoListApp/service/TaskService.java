package com.example.todoListApp.service;

import com.example.todoListApp.model.Task;
import com.example.todoListApp.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    public void completedTask(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if(optionalTask.isPresent()){
            Task task = optionalTask.get();
            task.setCompleted(true);
            taskRepository.save(task);
        }

    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
