/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.csp.signing.signature;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.signed.docflow.csp.signing.cryptoapiwrapper.CRYPT_DATA_BLOB;
import ru.it.lecm.signed.docflow.csp.signing.cryptoapiwrapper.JavaCryptoApiWrapper;
import ru.it.lecm.signed.docflow.csp.signing.cryptoapiwrapper.JavaCryptoApiWrapperMockImpl;
import ru.it.lecm.signed.docflow.csp.signing.exception.CryptoException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mariya German <german@alta.ru>
 */
public class CryptoApiWrapperSignatureProcessor implements SignatureProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoApiWrapperSignatureProcessor.class);

    private JavaCryptoApiWrapper cryptoApiWrapper = null;

    private void initWrapper(String wrapperfolder) {
        try {
            if (Platform.isWindows()) {
                String libName = Platform.is64Bit() ? "CryptoApiWrapper_x64.dll" : "CryptoApiWrapper_x86.dll";
                Map<String, Object> options = new HashMap<>();
                options.put(Library.OPTION_CALLING_CONVENTION, Function.ALT_CONVENTION);
                String libPath = Paths.get(wrapperfolder, libName).toString();
                cryptoApiWrapper = (JavaCryptoApiWrapper) Native.loadLibrary(libPath, JavaCryptoApiWrapper.class, options);
            } else if (Platform.isLinux()) {
                String libName = Platform.is64Bit() ? "libCryptoApiWrapper_x64.so" : "libCryptoApiWrapper_x86.so";
                cryptoApiWrapper = (JavaCryptoApiWrapper) Native.loadLibrary(Paths.get(wrapperfolder, libName).toString(), JavaCryptoApiWrapper.class);
            } else {
                cryptoApiWrapper = new JavaCryptoApiWrapperMockImpl();
            }
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            cryptoApiWrapper = new JavaCryptoApiWrapperMockImpl();
        }
    }

    public CryptoApiWrapperSignatureProcessor getInstanse(String wrapperfolder) {
        if (cryptoApiWrapper == null || cryptoApiWrapper instanceof JavaCryptoApiWrapperMockImpl) {
            initWrapper(wrapperfolder);
        }
        return this;
    }

    @Override
    public List<String> getAliasList() throws CryptoException {
        PointerByReference ref = new PointerByReference();
        IntByReference count = new IntByReference();
        int result = cryptoApiWrapper.getAliasList(ref, count);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        Pointer[] list = ref.getValue().getPointerArray(0, count.getValue());
        List<String> aliasList = new ArrayList<>(count.getValue());
        for(int i = 0; i < count.getValue(); i++) {
           String alias = list[i].getWideString(0);
           aliasList.add(alias);
        }
        cryptoApiWrapper.freeAliasList(ref.getValue(), count.getValue());
        return aliasList;
    }

    @Override
    public X509Certificate getX509Certificate(String alias) throws CryptoException {
        X509Certificate certificate = null;
        try {
            PointerByReference ref = new PointerByReference();
            int result = cryptoApiWrapper.getEncodedCertificate(new WString(alias), ref);
            if (result != 0) {
                throw new CryptoException(Integer.toString(result));
            }
            CRYPT_DATA_BLOB certBlob = new CRYPT_DATA_BLOB (ref.getValue());
            if (certBlob.pbData != null) {
                byte[] certBuf = new byte[certBlob.cbData];
                certBlob.pbData.read(0, certBuf, 0, certBlob.cbData);
                //byte[] certBuf = certBlob.pbData.getByteArray(0, certBlob.cbData).clone();
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                InputStream is = new ByteArrayInputStream(certBuf);
                certificate = (X509Certificate) certFactory.generateCertificate(is);
                cryptoApiWrapper.freeCRYPT_DATA_BLOB(ref.getValue());
            }
        } catch (CertificateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CryptoException(ex);
        }
        return certificate;
    }

    @Override
    public byte[] sign(byte[] data, String alias, String pinCode) throws CryptoException {
        Pointer pbData = new Memory(data.length);
        pbData.write(0, data, 0, data.length);
        CRYPT_DATA_BLOB dataBlob = new CRYPT_DATA_BLOB();
        dataBlob.pbData = pbData;
        dataBlob.cbData = data.length;

        PointerByReference ref = new PointerByReference();
        int result = cryptoApiWrapper.signData(dataBlob, new WString(alias), pinCode, ref);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        CRYPT_DATA_BLOB signatureBlob = new CRYPT_DATA_BLOB (ref.getValue());
        if (signatureBlob.pbData != null) {
            byte[] signatureBuf = new byte[signatureBlob.cbData];
            signatureBlob.pbData.read(0, signatureBuf, 0, signatureBlob.cbData);
            //byte[] signatureBuf = signatureBlob.pbData.getByteArray(0, signatureBlob.cbData);
            ArrayUtils.reverse(signatureBuf);
            cryptoApiWrapper.freeCRYPT_DATA_BLOB(ref.getValue());
            return signatureBuf;
        } else {
            return null;
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signData, X509Certificate certificate) throws CryptoException {
        boolean verificationStatus;
        try {
            Pointer pbData = new Memory(data.length);
            pbData.write(0, data, 0, data.length);
            CRYPT_DATA_BLOB dataBlob = new CRYPT_DATA_BLOB();
            dataBlob.pbData = pbData;
            dataBlob.cbData = data.length;

            ArrayUtils.reverse(signData);
            Pointer pbSignData = new Memory(signData.length);
            pbSignData.write(0, signData, 0, signData.length);
            CRYPT_DATA_BLOB signDataBlob = new CRYPT_DATA_BLOB();
            signDataBlob.pbData = pbSignData;
            signDataBlob.cbData = signData.length;

            byte[] certBuf = certificate.getEncoded();
            Pointer pbCert = new Memory(certBuf.length);
            pbCert.write(0, certBuf, 0, certBuf.length);
            CRYPT_DATA_BLOB certBlob = new CRYPT_DATA_BLOB();
            certBlob.pbData = pbCert;
            certBlob.cbData = certBuf.length;

            BOOLByReference ok = new BOOLByReference();
            int result = cryptoApiWrapper.verify(signDataBlob, certBlob,
                    dataBlob, ok);
            if (result != 0) {
                throw new CryptoException(Integer.toString(result));
            }
            verificationStatus = ok.getValue().booleanValue();
        } catch (CertificateEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CryptoException(ex);
        }
        return verificationStatus;
    }

    @Override
    public byte[] hashGostr3411(byte[] data) throws CryptoException {
        Pointer pbData = new Memory(data.length);
        pbData.write(0, data, 0, data.length);

        CRYPT_DATA_BLOB dataBlob = new CRYPT_DATA_BLOB();
        dataBlob.pbData = pbData;
        dataBlob.cbData = data.length;

        byte[] dataBuf = new byte[dataBlob.cbData];
        dataBlob.pbData.read(0, dataBuf, 0, dataBlob.cbData);

        PointerByReference ref = new PointerByReference();
        int result = cryptoApiWrapper.calculateHash(dataBlob, ref);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        return handlePointer(ref.getValue());
    }

    @Override
    public byte[] createCMSSignature(byte[] data, String alias, boolean isOnlySignature, String pinCode) throws CryptoException {
        Pointer pbData = new Memory(data.length);
        pbData.write(0, data, 0, data.length);

        CRYPT_DATA_BLOB dataBlob = new CRYPT_DATA_BLOB();
        dataBlob.pbData = pbData;
        dataBlob.cbData = data.length;

        PointerByReference ref = new PointerByReference();
        int result = cryptoApiWrapper.createPKCS7Signature(dataBlob, new WString(alias), isOnlySignature, pinCode, ref);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        return handlePointer(ref.getValue());
    }

    @Override
    public boolean verifyCMSSignature(byte[] data, byte[] signedMessage, X509Certificate cert, boolean isOnlySignature) throws CryptoException {
        boolean verificationStatus;
        try {
            Pointer pbData = new Memory(data.length);
            pbData.write(0, data, 0, data.length);
            CRYPT_DATA_BLOB dataBlob = new CRYPT_DATA_BLOB();
            dataBlob.pbData = pbData;
            dataBlob.cbData = data.length;

            //ArrayUtils.reverse(signData);
            Pointer pbSignData = new Memory(signedMessage.length);
            pbSignData.write(0, signedMessage, 0, signedMessage.length);
            CRYPT_DATA_BLOB signDataBlob = new CRYPT_DATA_BLOB();
            signDataBlob.pbData = pbSignData;
            signDataBlob.cbData = signedMessage.length;

            byte[] certBuf = cert.getEncoded();
            Pointer pbCert = new Memory(certBuf.length);
            pbCert.write(0, certBuf, 0, certBuf.length);
            CRYPT_DATA_BLOB certBlob = new CRYPT_DATA_BLOB();
            certBlob.pbData = pbCert;
            certBlob.cbData = certBuf.length;

            BOOLByReference ok = new BOOLByReference();
            int result = cryptoApiWrapper.verifyPKCS7Signature(signDataBlob,
                    certBlob, dataBlob, isOnlySignature, ok);
            if (result != 0) {
                throw new CryptoException(Integer.toString(result));
            }
            verificationStatus = ok.getValue().booleanValue();
        } catch (CertificateEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CryptoException(ex);
        }
        return verificationStatus;
    }

    private byte[] handlePointer(Pointer pointer) {
        byte[] result = null;
        CRYPT_DATA_BLOB blob = new CRYPT_DATA_BLOB(pointer);
        if (blob.pbData != null) {
            byte[] signatureBuf = new byte[blob.cbData];
            blob.pbData.read(0, signatureBuf, 0, blob.cbData);
            //byte[] signatureBuf = blob.pbData.getByteArray(0, blob.cbData);
            //ArrayUtils.reverse(signatureBuf);
            cryptoApiWrapper.freeCRYPT_DATA_BLOB(pointer);
            result = signatureBuf;
        }
        return result;
    }

}
