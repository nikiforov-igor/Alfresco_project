package ru.it.lecm.base.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author dbayandin
 */
public class LecmBaseUtilsServiceImpl extends BaseBean implements LecmBaseUtilsService {

    private static final int INDEX_KEY = 0;
    private static final int INDEX_IV = 1;
    private static final int ITERATIONS = 1;

    private static final int ARG_INDEX_FILENAME = 0;
    private static final int ARG_INDEX_PASSWORD = 1;
    
    private static final int SALT_OFFSET = 8;
    private static final int SALT_SIZE = 8;
    private static final int CIPHERTEXT_OFFSET = SALT_OFFSET + SALT_SIZE;
    private static final int KEY_SIZE_BITS = 128;
    
    private static final String MAGIC_WORD = "12345";
    
    private Map<QName, Serializable> propertiesMap;

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(LECM_SECRET_FOLDER_ID);
    }

    public void init() {
        this.propertiesMap = new HashMap<QName, Serializable>();

        NodeRef folderRef = getServiceRootFolder();
    }

    public Boolean checkProperties(NodeRef nodeRef, Map<QName, Serializable> properties) {
        Boolean result = false;

        return result;
    }

    //TODO замаскировать
    public Properties decrypt(NodeRef nodeRef) throws IOException {
        Properties result=null;
        //проверить существование\
        if (!(nodeService.exists(nodeRef) && nodeService.getType(nodeRef).isMatch(ContentModel.TYPE_CONTENT))) {
            throw new RuntimeException("File not exists");
        }
        
        //вытащить контент
        ContentService contentService = serviceRegistry.getContentService();
        ContentReader contentReader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
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
            //расшифровать
            byte[] content = Base64.decodeBase64(line);
            byte[] salt = Arrays.copyOfRange(
                    content, SALT_OFFSET, SALT_OFFSET + SALT_SIZE);
            
            byte[] encrypted = Arrays.copyOfRange(
                    content, CIPHERTEXT_OFFSET, content.length);
            
            Cipher cipher = OpenSSLCipherFactory.getInstance(MAGIC_WORD.getBytes(), salt, Cipher.DECRYPT_MODE, KEY_SIZE_BITS);
            byte[] decrypted = cipher.doFinal(encrypted);
            //разобрать    
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

}
