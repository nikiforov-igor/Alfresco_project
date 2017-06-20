/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.csp.signing.exception;

import java.security.cert.CertificateException;

/**
 *
 * @author rjagudin
 */
public class CryptoException extends Exception {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(CertificateException ex) {
        super(ex.getMessage());
    }

}
