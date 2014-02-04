package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public interface ArmService {
	public static final String ARM_ROOT_ID = "ARM_ROOT_ID";
	public static final String ARM_SETTINGS_DICTIONARY_NAME = "Настройки АРМ";

	/**
	 * Получение справочника с настройками АРМ
	 * @return Справочник с настройками АРМ
	 */
	public NodeRef getDictionaryArmSettings();
}
