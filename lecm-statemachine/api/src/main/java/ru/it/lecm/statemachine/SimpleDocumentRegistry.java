package ru.it.lecm.statemachine;

import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 * Created by pmelnikov on 09.04.2015.
 *
 * Реестр типов документов без жизненного цикла и их настроек
 */
public interface SimpleDocumentRegistry {

    /**
     * Регистрация типа документа без жизненного цикла
     * @param type - тип документа
     * @param item - настройки для работы документа
     * @throws LecmBaseException
     */
    void registerDocument(String type, SimpleDocumentRegistryItem item) throws LecmBaseException;

    /**
     * Проверка типа документа на отстусвие жизненного цикла
     * @param type
     * @return
     */
    boolean isSimpleDocument(QName type);
	
	/**
	 * Проверка на наличие корневой папки для типа документа
	 * Создаёт папку, если её нет и нарезает права в соответствии с указанными
	 * в МС
	 * 
	 * @param type - текстовое представление типа документа
	 * @param forceRebuildACL - если true, то права будут перенарезаны 
	 *							даже при наличии папки
	 * @throws WriteTransactionNeededException 
	 */
	void checkTypeFolder(String type, boolean forceRebuildACL) throws WriteTransactionNeededException;

}
