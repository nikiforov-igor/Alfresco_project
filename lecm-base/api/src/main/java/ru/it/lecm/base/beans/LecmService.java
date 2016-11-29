/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

/**
 * Интерфейс для наших сервисов. На момент написания нужен только для того,
 * чтобы выделить функционал по инициализации сервисов в одно место
 * @author ikhalikov
 */
public interface LecmService {
	
	/**
	 * В этом методе должна быть описана логика, которая вызывается на старте системы
	 * Не желательно оборачивать её в транзакцию, т.к в централизованном бутстрапе
	 * уже есть транзакция
	 */
	void initService();
	
}
