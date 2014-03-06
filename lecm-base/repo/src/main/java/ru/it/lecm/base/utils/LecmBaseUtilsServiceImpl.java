package ru.it.lecm.base.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import ru.it.lecm.base.beans.BaseBean;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import static ru.it.lecm.base.utils.LecmBaseUtilsService.LECM_PUBKEY_FILE_NAME;

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
        Properties result= new Properties();
        
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
        
            byte[] content = Base64.decodeBase64(originalText.toString());
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
				result = checkSignature(nodeRef, null) && new Date().before(propDate);
			} catch(ParseException e) {
				Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return result;
	}
	
	public Boolean checkSignature(final NodeRef nodeRef, final NodeRef signatureRef) {
		Boolean result = false;
		
		try {
			NodeRef folderRef = getServiceRootFolder();
			
			//read the signature
			NodeRef sigToVerifyRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, LECM_SIGNATURE_FILE_NAME);
			byte[] sigToVerify = readContent(sigToVerifyRef);

			//read the key
			NodeRef pubKeyRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, LECM_PUBKEY_FILE_NAME);
			byte[] pubKeyStr = readContent(pubKeyRef);
			
			//read the content
			NodeRef contentRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, LECM_LICENSE_FILE_NAME);
			byte[] contenStr = readContent(contentRef);
			
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(pubKey);
			signature.update(contenStr);
			result = signature.verify(sigToVerify);
		} catch(NoSuchAlgorithmException e) {
			Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		} catch(IOException e) {
			Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		} catch(InvalidKeySpecException e) {
			Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		} catch(InvalidKeyException e) {
			Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		} catch(SignatureException e) {
			Logger.getLogger(LecmBaseUtilsServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		}
		
		return result;
	}
	
	private byte[] readContent(final NodeRef nodeRef) throws IOException {
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
		byte[] result = IOUtils.toByteArray(is);
		is.close();
		
		return result;
	}
	
	public void genSig() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
		keyGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyGen.generateKeyPair();
		
		Signature signature = Signature.getInstance("SHA1withRSA", "BC");

		signature.initSign(keyPair.getPrivate(), new SecureRandom());

		NodeRef folderRef = getServiceRootFolder();
		NodeRef contentRef = nodeService.getChildByName(folderRef, ContentModel.ASSOC_CONTAINS, LECM_LICENSE_FILE_NAME);
		byte[] contenStr = readContent(contentRef);
		signature.update(contenStr);
		
		byte[] sigBytes = signature.sign();
		
		//save keys
		saveInFile(LECM_PRIVKEY_FILE_NAME, keyPair.getPrivate().getEncoded());
		saveInFile(LECM_PUBKEY_FILE_NAME, keyPair.getPublic().getEncoded());

		//save signature
		saveInFile(LECM_SIGNATURE_FILE_NAME, sigBytes);
	}

	private void saveInFile(String LECM_PRIVKEY_FILE_NAME, byte[] encoded) {
		//saving
	}
}
