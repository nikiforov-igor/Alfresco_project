package ru.it.lecm.workflow.api;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 *
 * @author vmalygin
 */
public interface WorkflowFoldersService {

	/**
	 * Создание и получение корневой папки сервиса Регламентов
	 * @return
	 */
	NodeRef getWorkflowFolder();

	/**
	 * Создание и получение папки для хранения глобальных(не привязанных к lecm-document:base) результатов работы Регламентов
	 * @return
	 */
	NodeRef getGlobalResultFolder();

	/**
	 * Создание и получение папки для хранения рабочих копий списков исполнителей регламентов
	 * @return
	 */
	NodeRef getAssigneesListWorkingCopyFolder();
}
