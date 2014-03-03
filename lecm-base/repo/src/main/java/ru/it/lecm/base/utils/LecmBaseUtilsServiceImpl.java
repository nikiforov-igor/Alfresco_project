package ru.it.lecm.base.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author dbayandin
 */
public class LecmBaseUtilsServiceImpl extends BaseBean implements LecmBaseUtilsService {

    private static final int SALT_OFFSET = 8;
    private static final int SALT_SIZE = 8;
    private static final int CIPHERTEXT_OFFSET = SALT_OFFSET + SALT_SIZE;
    private static final int KEY_SIZE_BITS = 128;
    
    private static final String MAGIC_WORD = "12345";
    
    private Map<QName, Serializable> propertiesMap;
	private Date nextRefreshDate;
	
	private NamespaceService namespaceService;
	//private ContentService contentService;
	
	public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
	/*
	public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }
	*/
	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(LECM_SECRET_FOLDER_ID);
	}

    //TODO Замаскировать
    //TODO как следует обработать исключения
    public Properties decrypt(final NodeRef nodeRef) throws IOException {
        Properties result=null;
        
        if (!(nodeService.exists(nodeRef) && nodeService.getType(nodeRef).isMatch(ContentModel.TYPE_CONTENT))) {
            throw new RuntimeException("File not exists");
        }
        
        ContentReader contentReader = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<ContentReader> () {
			@Override
			public ContentReader doWork () throws Exception {
				ContentService contentService = serviceRegistry.getContentService();
				return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
			}
		}, AuthenticationUtil.getSystemUserName());
		
        if (!contentReader.exists()) {
            throw new RuntimeException("File not exists");
        }
        InputStream is = contentReader.getContentInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder originalText = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                originalText.append(line);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            bufferedReader.close();
        }
        try {
        
            byte[] content = Base64.decodeBase64(line);
            byte[] salt = Arrays.copyOfRange(
                    content, SALT_OFFSET, SALT_OFFSET + SALT_SIZE);
            
            byte[] encrypted = Arrays.copyOfRange(
                    content, CIPHERTEXT_OFFSET, content.length);
            
            Cipher cipher = OpenSSLCipherFactory.getInstance(MAGIC_WORD.getBytes(), salt, Cipher.DECRYPT_MODE, KEY_SIZE_BITS);
            byte[] decrypted = cipher.doFinal(encrypted);
        
            result = new Properties();
            result.load(new ByteArrayInputStream(decrypted));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

	private void refreshPropertiesMap() throws IOException {
		this.propertiesMap = new HashMap<QName, Serializable>();
		
		NodeRef folderRef = getServiceRootFolder();
		NodeRef nodeRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, LECM_LICENSE_FILE_NAME);
		if (nodeRef != null) { 
			Map properties = decrypt(nodeRef);
			for (Iterator propNameIterator = properties.keySet().iterator(); propNameIterator.hasNext();) {
				String propName = (String) propNameIterator.next();
				String propValue = (String) properties.get(propName);
				
				QName propQName= QName.createQName("http://www.alfresco.org/model/content/1.0", propName);
				if (propQName != null && propValue != null) 
					propertiesMap.put(propQName, propValue);
			}
			this.nextRefreshDate = new Date(new Date().getTime() + PROPS_REFRESH_PERIOD);
		}
	}
		
	public void init() {
		try {
			refreshPropertiesMap();
		} catch(Exception e) {
			Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public Boolean checkProperties(NodeRef nodeRef, Map<QName, Serializable> properties) {
		Boolean result = false;
		Date currentDate = new Date();
		if (this.nextRefreshDate == null || currentDate.after(this.nextRefreshDate)) {
			try {
				refreshPropertiesMap();
			} catch(IOException e) {
				Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		if (this.propertiesMap != null && 
			!this.propertiesMap.isEmpty() && 
			this.propertiesMap.containsKey(PROP_EXPIRATION_DATE)) {
			
			try {
				Date propDate = COMMON_DATE_FORMAT.parse((String)this.propertiesMap.get(PROP_EXPIRATION_DATE));
				result = new Date().before(propDate);
			} catch(ParseException e) {
				Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return result;
	}
}
