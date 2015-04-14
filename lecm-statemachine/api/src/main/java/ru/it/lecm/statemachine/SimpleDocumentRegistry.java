package ru.it.lecm.statemachine;

import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.LecmBaseException;

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
    void registerDocument(QName type, SimpleDocumentRegistryItem item) throws LecmBaseException;

    /**
     * Проверка типа документа на отстусвие жизненного цикла
     * @param type
     * @return
     */
    boolean isSimpleDocument(QName type);

}
