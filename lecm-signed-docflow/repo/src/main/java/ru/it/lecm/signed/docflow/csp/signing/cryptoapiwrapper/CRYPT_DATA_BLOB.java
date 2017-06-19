/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.csp.signing.cryptoapiwrapper;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author Mariya German <german@alta.ru>
 */
public class CRYPT_DATA_BLOB extends Structure implements Structure.ByReference {

    public int cbData;
    public Pointer pbData;

    public CRYPT_DATA_BLOB() {
    }

    public CRYPT_DATA_BLOB(Pointer p) {
        super(p);
        read();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("cbData", "pbData");
    }

}
