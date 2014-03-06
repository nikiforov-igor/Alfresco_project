package ru.it.lecm.base.utils;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author dbayandin
 */
public interface LecmBaseUtilsService {
	
	public static final String LECM_SECRET_FOLDER_NAME = "Secret folder";
	public static final String LECM_SECRET_FOLDER_ID = "LECM_SECRET_FOLDER_ID";
	
	public static final String LECM_SIGNATURE_FILE_NAME = "signature";
	public static final String LECM_LICENSE_FILE_NAME = "license";
	public static final String LECM_PUBKEY_FILE_NAME = "key";
	public static final String LECM_PRIVKEY_FILE_NAME = "private_key";
	
	public static final DateFormat COMMON_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	public static final long PROPS_REFRESH_PERIOD = 86400000;
	
	public static final QName PROP_EXPIRATION_DATE = QName.createQName("http://www.alfresco.org/model/content/1.0", "expiration_date");
	public static final QName PROP_CUSTOMER = QName.createQName("http://www.alfresco.org/model/content/1.0", "customer");
	
    public Boolean checkProperties(NodeRef nodeRef, Map<QName, Serializable> properties);
}
