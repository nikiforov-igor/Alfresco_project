/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.csp.signing.cryptoapiwrapper;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 *
 * @author Mariya German <german@alta.ru>
 */
public interface JavaCryptoApiWrapper extends Library {

    int getAliasList(PointerByReference list, IntByReference size);

    int getEncodedCertificate(WString alias, PointerByReference certificateBlob);

    int signData(CRYPT_DATA_BLOB data, WString alias, String pin, PointerByReference signatureBlob);

    int verify(CRYPT_DATA_BLOB signatureBlob, CRYPT_DATA_BLOB certificateBlob,
            CRYPT_DATA_BLOB dataBlob, BOOLByReference ok);

    int calculateHash(CRYPT_DATA_BLOB dataBlob, PointerByReference hashBlob);

    int createPKCS7Signature(CRYPT_DATA_BLOB dataBlob, WString alias, boolean onlySignature,
            String pin, PointerByReference signatureBlob);

    int verifyPKCS7Signature(CRYPT_DATA_BLOB signatureBlob, CRYPT_DATA_BLOB certificateBlob,
            CRYPT_DATA_BLOB dataBlob, boolean onlySignature, BOOLByReference ok);

    void freeCRYPT_DATA_BLOB(Pointer blob);

    void freeAliasList(Pointer list, int sizeList);
}
