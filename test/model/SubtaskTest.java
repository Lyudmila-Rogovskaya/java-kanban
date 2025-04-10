package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void subtaskEqualityByIdTest() { // равенство подзадач по id
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.DONE, 1);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

}
