package ru.it.lecm.signed.docflow.csp.signing.cryptoapiwrapper;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaCryptoApiWrapperMockImpl implements JavaCryptoApiWrapper {

    static final Logger _log = LoggerFactory.getLogger(JavaCryptoApiWrapperMockImpl.class);

    @Override
    public int getAliasList(PointerByReference list, IntByReference size) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public int getEncodedCertificate(WString alias, PointerByReference certificateBlob) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public int signData(CRYPT_DATA_BLOB data, WString alias, String pin, PointerByReference signatureBlob) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public int verify(CRYPT_DATA_BLOB signatureBlob, CRYPT_DATA_BLOB certificateBlob,
            CRYPT_DATA_BLOB dataBlob, WinDef.BOOLByReference ok) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public int calculateHash(CRYPT_DATA_BLOB dataBlob, PointerByReference hashBlob) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public int createPKCS7Signature(CRYPT_DATA_BLOB dataBlob, WString alias, boolean onlySignature,
            String pin, PointerByReference signatureBlob) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public int verifyPKCS7Signature(CRYPT_DATA_BLOB signatureBlob, CRYPT_DATA_BLOB certificateBlob,
            CRYPT_DATA_BLOB dataBlob, boolean onlySignature, WinDef.BOOLByReference ok) {
        _log.warn("Mock has been called");
        return -1;
    }

    @Override
    public void freeCRYPT_DATA_BLOB(Pointer blob) {
        _log.warn("Mock has been called");
    }

    @Override
    public void freeAliasList(Pointer list, int sizeList) {
        _log.warn("Mock has been called");
    }
}
