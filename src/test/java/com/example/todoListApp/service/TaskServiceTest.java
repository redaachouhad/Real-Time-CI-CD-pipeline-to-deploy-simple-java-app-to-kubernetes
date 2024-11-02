package com.example.todoListApp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import com.example.todoListApp.model.Task;
import com.example.todoListApp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task(1L, "Task 1", false);
    }

    @Test
    @Order(1)
    void findAllTasks() {
        // precondition
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        //Action
        List<Task> result = taskService.findAllTasks();

        // verify
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAll();

    }

    @Test
    @Order(2)
    void saveTask() {
        // precondition
        // ---
        // action
            taskService.saveTask(task);
        // verify
        verify(taskRepository, times(1)).save(task);



    }

    @Test
    @Order(3)
    void completedTask_TaskExists() {
        // precondition
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        // action
        taskService.completedTask(1L);
        // verify
        assertTrue(task.getCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);

    }

    @Test
    @Order(4)
    void completedTask_TaskNotFound() {
        // precondition
        // ---
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        // action
        taskService.completedTask(1L);
        // verify
        verify(taskRepository, times(1)).findById(1L);

    }

    @Test
    @Order(5)
    void deleteTask() {
        // precondition
        // ---
        // action
        taskService.deleteTask(1L);
        // verify
        verify(taskRepository, times(1)).deleteById(1L);
    }
}