/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.wcalendar;

import ru.it.lecm.businessjournal.beans.EventCategory;

/**
 *
 * @author ikhalikov
 */
public interface CalendarCategory extends EventCategory{
    /**
     * Настройка производственного календаря
     */
    String SET_CALENDAR = "SET_CALENDAR";
    
    /**
     * Создание типового графика работы
     */
    String NEW_SHEDULE = "NEW_SHEDULE";
    
    /**
     * Создание индивидуального графика работы
     */
    String NEW_INDIVIDUAL_SHEDULE = "NEW_INDIVIDUAL_SHEDULE";
    
    /**
     * Удаление графика работы
     */
    String DELETE_SHEDULE = "DELETE_SHEDULE";
    
    /**
     * Редактирование типового графика работы
     */
    String EDIT_SHEDULE = "EDIT_SHEDULE";
    
    /**
     * Редактирование индивидуального графика работы
     */
    String EDIT_INDIVIDUAL_SHEDULE = "EDIT_INDIVIDUAL_SHEDULE";
    
    /**
     * Планирование отсутствия
     */
    String ADD_ABSENCE = "ADD_ABSENCE";
    
    /**
     * Удаление отсутствия
     */
    String DELETE_ABSENCE = "DELETE_ABSENCE";
    
    /**
     * Старт «Меня нет в офисе»
     */
    String START_NOT_IN_OFFICE = "START_NOT_IN_OFFICE";
    
    /**
     * Отмена «Меня нет в офисе»
     */
    String STOP_NOT_IN_OFFICE = "STOP_NOT_IN_OFFICE";
}
