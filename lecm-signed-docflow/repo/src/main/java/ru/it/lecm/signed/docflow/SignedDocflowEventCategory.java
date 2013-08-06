package ru.it.lecm.signed.docflow;

import ru.it.lecm.businessjournal.beans.EventCategory;

/**
 *
 * @author vlevin
 */
public interface SignedDocflowEventCategory extends EventCategory {

	/**
	 * Электронная подпись.
	 */
	String SIGNATURE = "SIGNATURE";
}
