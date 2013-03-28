package ru.it.lecm.statemachine;

import ru.it.lecm.businessjournal.beans.EventCategory;

public interface StateMachineEventCategory extends EventCategory {
    /**
     * Запуск бизнес-процесса
     */
    String START_WORKFLOW = "START_WORKFLOW";
    /**
     * Завершение бизнес-процесса
     */
    String END_WORKFLOW = "END_WORKFLOW";
}
