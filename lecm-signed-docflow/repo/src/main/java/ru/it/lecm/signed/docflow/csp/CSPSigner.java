/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.csp;

import ru.it.lecm.csp.signing.client.cryptoapiwrapper.JavaCryptoApiWrapperMockImpl;
import ru.it.lecm.csp.signing.client.signature.CryptoApiWrapperSignatureProcessor;
import ru.it.lecm.csp.signing.client.signature.SignatureProcessor;

/**
 *
 * @author rjagudin
 */
public class CSPSigner {

    private CSPSigner() {

    }

    private static SignatureProcessor signatureProcessor;

    public CSPSigner(String dllpath) {
        if (signatureProcessor == null || (signatureProcessor instanceof JavaCryptoApiWrapperMockImpl)) {
            signatureProcessor = new CryptoApiWrapperSignatureProcessor().getInstanse(dllpath);
        }
    }

    public SignatureProcessor getProcessor() {
        return signatureProcessor;
    }

}
