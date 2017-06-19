/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.csp.signing.signature;

import java.security.cert.X509Certificate;
import java.util.List;
import ru.it.lecm.signed.docflow.csp.signing.exception.CryptoException;

/**
 *
 * @author MGerman
 */
public interface SignatureProcessor {

    /**
     * Возвращает список имен доступных контейнеров ЭП.
     *
     * @return список имен доступных контейнеров ЭП
     * @throws CryptoException
     */
    public List<String> getAliasList() throws CryptoException;

    /**
     * Возвращает сертификат, связанный с данным контейнером ЭП.
     *
     * @param alias Наименование контейнера ЭП
     * @return Сертификат или null, если данный контейнер не существует или не содержит сертификат.
     * @throws CryptoException
     */
    public X509Certificate getX509Certificate(String alias) throws CryptoException;

    /**
     * Подписывает данные по ГОСТ Р 34.11 / 34.10-2001
     *
     * @param data данные, необходимые подписать
     * @param alias наименование контейнера используемой ЭП
     * @param pinCode пинкод для доступа к контейнеру используемой ЭП
     * @return результат подписания по ГОСТ Р 34.11 / 34.10-2001
     * @throws CryptoException
     */
    public byte[] sign(byte[] data, String alias, String pinCode) throws CryptoException;

    /**
     * Проверяет подпись по ГОСТ Р 34.11 / 34.10-2001
     *
     * @param data исходные данные, на основании которыхь необходимо проверить подпись
     * @param signData подписанные данные
     * @param certificate сертификат, которым необходимо проверить подпись.
     * @return результат проверки подписи. true - подпись валидна, false - подпись не валидна.
     * @throws CryptoException
     */
    public boolean verify(byte[] data, byte[] signData, X509Certificate certificate) throws CryptoException;

    /**
     * Генерирует хеш-сумму по ГОСТ ГОСТ Р 34.11
     *
     * @param data данные на которых считается хэш
     * @return результат генерации хэш-суммы
     * @throws CryptoException
     */
    public byte[] hashGostr3411(byte[] data) throws CryptoException;

    /**
     * Создает сообщение в формате CADES-BES
     *
     * @param data исходные данные, которые необходимо подписать
     * @param alias наименование контейнера, используемой ЭП
     * @param isOnlySignature флаг информирующий о добавлении исходных данных в результирующее
     * сообщение. true - сообщение будет содержать только подписанные данные, false - сообщение
     * будет содержать исходные данные.
     * @param pinCode пинкод для доступа к контейнеру используемой ЭП
     * @return сообщение в формате CADES-BES
     * @throws CryptoException
     */
    public byte[] createCMSSignature(byte[] data, String alias, boolean isOnlySignature, String pinCode) throws CryptoException;

    /**
     * Проверяет сообщение ы формате CADES-BES
     *
     * @param data исходные данные
     * @param signedMessage подписанное сообщение в формате CADES-BES
     * @param cert сертификат, которым необходимо проверить подпись
     * @param isOnlySignature флаг информирующий о содержании исходных данных в подписанном
     * сообщение. true - сообщение содержит только подписанные данные, false - сообщение содержит
     * исходные данные.
     * @return результат проверки подписи. true - подпись валидна, false - подпись не валидна.
     * @throws CryptoException
     */
    public boolean verifyCMSSignature(byte[] data, byte[] signedMessage, X509Certificate cert, boolean isOnlySignature) throws CryptoException;

}
