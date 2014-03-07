package ru.it.lecm.platform.utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

/**
 * Hello world!
 *
 */
public class LicenseUtility 
{
	private static String PUBLIC_KEY_FILENAME = "key";
	private static String PRIVATE_KEY_FILENAME = "private_key";
	private static String SIGNATURE_FILENAME = "signature";
	private static String CONTENT_FILENAME = "license";
	
    public static void main( String[] args )
    {
		if (args.length != 4) {
            System.out.println("Usage: mode(sign/verify) publickeyfile signaturefile datafile");
        } else try {
			
			PUBLIC_KEY_FILENAME = args[1];
			SIGNATURE_FILENAME = args[2];
			CONTENT_FILENAME = args[3];
			
			String mode = args[0];
			Boolean result = false;
			Security.addProvider(new BouncyCastleProvider());
			
			if ("sign".equals(mode)) {
				genSig();
				System.out.println(String.format("Signature and keys was successfully generated"));
			} if ("verify".equals(mode)) {
				result = checkSignature();
				System.out.println(String.format("Signature was successfully verifyed, result: %b", result));
			}
			
			
        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
		
    }

	private static byte[] readPemKey(String string) throws FileNotFoundException, IOException {
		FileInputStream keyfis = new FileInputStream(string);

		PEMParser parser = new PEMParser(new InputStreamReader(keyfis));
		Object publicKey = parser.readObject();
		
		// Cast to a PEMKeyPair
		SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) publicKey;

		// Get the encoded objects ready for conversion to Java objects
		byte[] encodedPublicKey = publicKeyInfo.getEncoded();
		return encodedPublicKey;
	}
	
	private static byte[] readSignature(String string) throws FileNotFoundException, IOException {
		FileInputStream sigfis = new FileInputStream(string);
		
		PEMParser parser = new PEMParser(new InputStreamReader(sigfis));
		ContentInfo signature = (ContentInfo) parser.readObject();
				
		byte[] sigToVerify = signature.getEncoded();
		return sigToVerify;
	}
	
	public static Boolean checkSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, FileNotFoundException, IOException, NoSuchProviderException, InvalidKeySpecException {
		Boolean result = false;
		
		//byte[] publicKeyData = readKey(PUBLIC_KEY_FILENAME);
		byte[] publicKeyData = readFromFile(PUBLIC_KEY_FILENAME);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		
		
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(publicKey);
			
		FileInputStream datafis = new FileInputStream(CONTENT_FILENAME);
		BufferedInputStream bufin = new BufferedInputStream(datafis);
		byte[] buffer = new byte[1024];
		int len;
		while (bufin.available() != 0) {
			len = bufin.read(buffer);
			sig.update(buffer, 0, len);
		};
		bufin.close();
		
		//byte[] signatureData = readSignature(SIGNATURE_FILENAME);
		byte[] signatureData = readFromFile(SIGNATURE_FILENAME);
		result = sig.verify(signatureData);
		
		return result;
	}
	
	public static void genSig() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
		keyGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyGen.generateKeyPair();
		
		Signature signature = Signature.getInstance("SHA1withRSA", "BC");

		signature.initSign(keyPair.getPrivate(), new SecureRandom());

		FileInputStream datafis = new FileInputStream(CONTENT_FILENAME);
		BufferedInputStream bufin = new BufferedInputStream(datafis);
		byte[] buffer = new byte[1024];
		int len;
		while (bufin.available() != 0) {
			len = bufin.read(buffer);
			signature.update(buffer, 0, len);
		};
		bufin.close();

		byte[] sigBytes = signature.sign();
		
		//save keys
		saveInFile(PRIVATE_KEY_FILENAME, keyPair.getPrivate().getEncoded());
		saveInFile(PUBLIC_KEY_FILENAME, keyPair.getPublic().getEncoded());

		//save signature
		saveInFile(SIGNATURE_FILENAME, sigBytes);
	}

	private static void saveInFile(String filePath, byte[] contentBytes) throws IOException {
		File privateKeyFile = new File(filePath);
		privateKeyFile.createNewFile();
		FileOutputStream privateKeyOs = new FileOutputStream(privateKeyFile);
		privateKeyOs.write(contentBytes);
		privateKeyOs.close();
	}
	
	public static byte[] readFromFile(String filePath) throws FileNotFoundException, IOException {
		FileInputStream datafis = new FileInputStream(filePath);
		byte[] result = new byte[datafis.available()]; 
		datafis.read(result);
		datafis.close();
		return result;
	}
}
