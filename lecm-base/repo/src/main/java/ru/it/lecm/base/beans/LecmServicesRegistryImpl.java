/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Реестр lecm сервисов, на момент написания нужен только для обеспечения
 * централизованной инициализации сервисов
 * 
 * @author ikhalikov
 */
public class LecmServicesRegistryImpl extends AbstractLifecycleBean implements LecmServicesRegistry {
	
	private final Map<String, LecmService> services = new HashMap<>();
	
	private static final Logger logger = LoggerFactory.getLogger(LecmServicesRegistryImpl.class);
	private LecmTransactionHelper lecmTransactionHelper;

	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}
	
	/**
	 * Сохранение сервиса в списке, в качестве ключа берётся имя класса
	 * @param service 
	 */
	@Override
	public void register(LecmService service) {
		services.put(service.getClass().getSimpleName(), service);
	}
	
	/**
	 * Централизованная инициализация всех сервисов
	 * @param ae 
	 */
	@Override
	protected void onBootstrap(ApplicationEvent ae) {
		lecmTransactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
					@Override
					public Void doWork() throws Exception {
						for (Map.Entry<String, LecmService> entry : services.entrySet()) {
							String name = entry.getKey();
							final LecmService service = entry.getValue();

							logger.debug("Going to bootstrap service {}", name);
							service.initService();
						}
						return null;
					}
				});
			}
		}, false);
	}

	@Override
	protected void onShutdown(ApplicationEvent ae) {
	}

	
}
