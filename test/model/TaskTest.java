package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void taskEqualityByIdTest() { // // равенство задач по id
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.DONE);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

}
