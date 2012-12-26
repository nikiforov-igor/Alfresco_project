/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.wcalendar;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface IWCalCommon {

	/**
	 * получение ссылки на папку, в которой хранятся календари
	 *
	 * @return nodeRef
	 */
	NodeRef getWCalendarContainer();

	IWCalCommon getWCalendarDescriptor();

	QName getWCalendarItemType();
}
