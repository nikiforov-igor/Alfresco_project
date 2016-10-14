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

    public static SignatureProcessor getProcessor(final String dllpath) {
        SignatureProcessor localInstance = signatureProcessor;
		if (localInstance == null || (signatureProcessor instanceof JavaCryptoApiWrapperMockImpl)) {
			synchronized (CSPSigner.class) {
				localInstance = signatureProcessor;
				if (localInstance == null) {
					signatureProcessor = localInstance = new CryptoApiWrapperSignatureProcessor().getInstanse(dllpath);
				}
			}
		}
		return localInstance;
    }

}
