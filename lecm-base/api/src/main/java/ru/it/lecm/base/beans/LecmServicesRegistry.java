/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

/**
 * Реестр lecm сервисов, на момент написания нужен только для обеспечения
 * централизованной инициализации сервисов
 * @author ikhalikov
 */
public interface LecmServicesRegistry {		
	
	/**
	 * Регистрация сервиса в списке
	 * @param service 
	 */
	public void register(LecmService service);		
	
}
