package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void epicEqualityByIdTest() { // равенство эпиков по id
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

}
