/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.csp.signing.client.signature;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.csp.signing.client.cryptoapiwrapper.CRYPT_DATA_BLOB;
import ru.it.lecm.csp.signing.client.cryptoapiwrapper.JavaCryptoApiWrapper;
import ru.it.lecm.csp.signing.client.cryptoapiwrapper.JavaCryptoApiWrapperMockImpl;
import ru.it.lecm.csp.signing.client.exception.CryptoException;

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

    private static final Logger _log = LoggerFactory.getLogger(CryptoApiWrapperSignatureProcessor.class);

    private static JavaCryptoApiWrapper _cryptoApiWrapper = null;
    /*
    static {
        try {
            if (Platform.isWindows()) {
                String libName = "CryptoApiWrapper.dll";
                if (Platform.is64Bit()) {
                    libName = "CryptoApiWrapper_x64.dll";
                }
                Map options = new HashMap();
                options.put(Library.OPTION_CALLING_CONVENTION, Function.ALT_CONVENTION);
                String libPath = Paths.get(libName, more) Utils.getLoadLib(libName, Utils.getAltaPath());
                _cryptoApiWrapper = (MyCryptoApiWrapper) Native.loadLibrary(libPath,
                        MyCryptoApiWrapper.class, options);
            } else if (Platform.isLinux()) {
//                String libPath = Utils.getLoadLib("libCryptoApiWrapper.so", "/home/pechenko");
                _cryptoApiWrapper = (MyCryptoApiWrapper) Native.loadLibrary("libCryptoApiWrapper.so",
                        MyCryptoApiWrapper.class);
            } else {
                _cryptoApiWrapper = new MyCryptoApiWrapperMockImpl();
            }
        } catch (Throwable ex) {
            _log.error(ex.getMessage(), ex);
            _cryptoApiWrapper = new MyCryptoApiWrapperMockImpl();
        }
    }
    */
    private void initWrapper(String wrapperfolder) {
        try {
            String libName;
            if (Platform.isWindows()) {
                libName = "CryptoApiWrapper_x86.dll";
                if (Platform.is64Bit()) {
                    libName = "CryptoApiWrapper_x64.dll";
                }
                Map options = new HashMap();
                options.put(Library.OPTION_CALLING_CONVENTION, Function.ALT_CONVENTION);
                String libPath = Paths.get(wrapperfolder, libName).toString();
                _cryptoApiWrapper = (JavaCryptoApiWrapper) Native.loadLibrary(libPath,
                        JavaCryptoApiWrapper.class, options);
            } else if (Platform.isLinux()) {
                libName = "libCryptoApiWrapper_x86.so";
                if (Platform.is64Bit()) {
                    libName = "libCryptoApiWrapper_x64.so";
                }
                _cryptoApiWrapper = (JavaCryptoApiWrapper) Native.loadLibrary(Paths.get(wrapperfolder, libName).toString()/*"libCryptoApiWrapper.so"*/,
                        JavaCryptoApiWrapper.class);
            } else {
                _cryptoApiWrapper = new JavaCryptoApiWrapperMockImpl();
            }
        } catch (Throwable ex) {
            _log.error(ex.getMessage(), ex);
            _cryptoApiWrapper = new JavaCryptoApiWrapperMockImpl();
        }
    }

    public CryptoApiWrapperSignatureProcessor getInstanse(String wrapperfolder) {
        if (_cryptoApiWrapper == null || _cryptoApiWrapper instanceof JavaCryptoApiWrapperMockImpl) {
            initWrapper(wrapperfolder);
        }
        return this;
    }

    @Override
    public List<String> getAliasList() throws CryptoException {
        PointerByReference ref = new PointerByReference();
        IntByReference count = new IntByReference();
        int result = _cryptoApiWrapper.getAliasList(ref, count);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        Pointer[] list = ref.getValue().getPointerArray(0, count.getValue());
        List<String> aliasList = new ArrayList<>(count.getValue());
        for(int i = 0; i < count.getValue(); i++) {
           String alias = list[i].getWideString(0);
           aliasList.add(alias);
        }
        _cryptoApiWrapper.freeAliasList(ref.getValue(), count.getValue());
        return aliasList;
    }

    @Override
    public X509Certificate getX509Certificate(String alias) throws CryptoException {
        X509Certificate certificate = null;
        try {
            PointerByReference ref = new PointerByReference();
            int result = _cryptoApiWrapper.getEncodedCertificate(new WString(alias), ref);
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
                _cryptoApiWrapper.freeCRYPT_DATA_BLOB(ref.getValue());
            }
        } catch (CertificateException ex) {
            _log.error(ex.getMessage(), ex);
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
        int result = _cryptoApiWrapper.signData(dataBlob, new WString(alias), pinCode, ref);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        CRYPT_DATA_BLOB signatureBlob = new CRYPT_DATA_BLOB (ref.getValue());
        if (signatureBlob.pbData != null) {
            byte[] signatureBuf = new byte[signatureBlob.cbData];
            signatureBlob.pbData.read(0, signatureBuf, 0, signatureBlob.cbData);
            //byte[] signatureBuf = signatureBlob.pbData.getByteArray(0, signatureBlob.cbData);
            ArrayUtils.reverse(signatureBuf);
            _cryptoApiWrapper.freeCRYPT_DATA_BLOB(ref.getValue());
            return signatureBuf;
        } else {
            return null;
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signData, X509Certificate certificate) throws CryptoException {

        boolean verificationStatus = false;
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
            int result = _cryptoApiWrapper.verify(signDataBlob, certBlob,
                    dataBlob, ok);
            if (result != 0) {
                throw new CryptoException(Integer.toString(result));
            }
            verificationStatus = ok.getValue().booleanValue();
        } catch (CertificateEncodingException ex) {
            _log.error(ex.getMessage(), ex);
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
        int result = _cryptoApiWrapper.calculateHash(dataBlob, ref);
        if (result != 0) {
            throw new CryptoException(Integer.toString(result));
        }
        CRYPT_DATA_BLOB hashBlob = new CRYPT_DATA_BLOB (ref.getValue());
        if (hashBlob.pbData != null) {
            byte[] hashBuf = new byte[hashBlob.cbData];
            hashBlob.pbData.read(0, hashBuf, 0, hashBlob.cbData);
            //byte[] hashBuf = hashBlob.pbData.getByteArray(0, hashBlob.cbData);
            //ArrayUtils.reverse(hashBuf);
            _cryptoApiWrapper.freeCRYPT_DATA_BLOB(ref.getValue());
            return hashBuf;
        } else  {
            return null;
        }
    }

    @Override
    public byte[] createCMSSignature(byte[] data, String alias, boolean isOnlySignature, String pinCode)
            throws CryptoException {
            Pointer pbData = new Memory(data.length);
            pbData.write(0, data, 0, data.length);
            CRYPT_DATA_BLOB dataBlob = new CRYPT_DATA_BLOB();
            dataBlob.pbData = pbData;
            dataBlob.cbData = data.length;

            PointerByReference ref = new PointerByReference();
            int result = _cryptoApiWrapper.createPKCS7Signature(dataBlob, new WString(alias),
                    isOnlySignature, pinCode, ref);
            if (result != 0) {
                throw new CryptoException(Integer.toString(result));
            }
            CRYPT_DATA_BLOB signatureBlob = new CRYPT_DATA_BLOB (ref.getValue());
            if (signatureBlob.pbData != null) {
            byte[] signatureBuf = new byte[signatureBlob.cbData];
            signatureBlob.pbData.read(0, signatureBuf, 0, signatureBlob.cbData);
            //byte[] signatureBuf = signatureBlob.pbData.getByteArray(0, signatureBlob.cbData);
            //ArrayUtils.reverse(signatureBuf);
            _cryptoApiWrapper.freeCRYPT_DATA_BLOB(ref.getValue());
            return signatureBuf;
        } else {
            return null;
        }
    }

    @Override
    public boolean verifyCMSSignature(byte[] data, byte[] signedMessage, X509Certificate cert,
            boolean isOnlySignature) throws CryptoException {
        boolean verificationStatus = false;
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
            int result = _cryptoApiWrapper.verifyPKCS7Signature(signDataBlob,
                    certBlob, dataBlob, isOnlySignature, ok);
            if (result != 0) {
                throw new CryptoException(Integer.toString(result));
            }
            verificationStatus = ok.getValue().booleanValue();
        } catch (CertificateEncodingException ex) {
            _log.error(ex.getMessage(), ex);
            throw new CryptoException(ex);
        }
        return verificationStatus;
    }

}
