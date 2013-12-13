
package ru.it.lecm.businessjournal.remote;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.it.lecm.businessjournal.remote package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetLastRecordsResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getLastRecordsResponse");
    private final static QName _Save_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "save");
    private final static QName _GetRecordsByInterval_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsByInterval");
    private final static QName _GetNodeById_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getNodeById");
    private final static QName _GetRecordsByIntervalResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsByIntervalResponse");
    private final static QName _GetRecordsAfterResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsAfterResponse");
    private final static QName _GetHistory_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getHistory");
    private final static QName _GetRecordsAfter_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsAfter");
    private final static QName _GetRecordsCount_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsCount");
    private final static QName _GetRecordsByParamsResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsByParamsResponse");
    private final static QName _GetRecordsCountResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsCountResponse");
    private final static QName _GetRecords_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecords");
    private final static QName _GetHistoryResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getHistoryResponse");
    private final static QName _GetRecordsResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsResponse");
    private final static QName _GetHistoryByCategoryResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getHistoryByCategoryResponse");
    private final static QName _GetRecordsByParams_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getRecordsByParams");
    private final static QName _SaveResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "saveResponse");
    private final static QName _MoveRecordToArchive_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "moveRecordToArchive");
    private final static QName _GetNodeByIdResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getNodeByIdResponse");
    private final static QName _GetLastRecords_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getLastRecords");
    private final static QName _GetHistoryByCategory_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "getHistoryByCategory");
    private final static QName _MoveRecordToArchiveResponse_QNAME = new QName("http://remote.businessjournal.lecm.it.ru/", "moveRecordToArchiveResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.it.lecm.businessjournal.remote
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetRecords }
     * 
     */
    public GetRecords createGetRecords() {
        return new GetRecords();
    }

    /**
     * Create an instance of {@link GetRecords.Filter }
     * 
     */
    public GetRecords.Filter createGetRecordsFilter() {
        return new GetRecords.Filter();
    }

    /**
     * Create an instance of {@link GetRecordsCount }
     * 
     */
    public GetRecordsCount createGetRecordsCount() {
        return new GetRecordsCount();
    }

    /**
     * Create an instance of {@link GetRecordsCount.Filter }
     * 
     */
    public GetRecordsCount.Filter createGetRecordsCountFilter() {
        return new GetRecordsCount.Filter();
    }

    /**
     * Create an instance of {@link Save }
     * 
     */
    public Save createSave() {
        return new Save();
    }

    /**
     * Create an instance of {@link GetLastRecordsResponse }
     * 
     */
    public GetLastRecordsResponse createGetLastRecordsResponse() {
        return new GetLastRecordsResponse();
    }

    /**
     * Create an instance of {@link GetRecordsByIntervalResponse }
     * 
     */
    public GetRecordsByIntervalResponse createGetRecordsByIntervalResponse() {
        return new GetRecordsByIntervalResponse();
    }

    /**
     * Create an instance of {@link GetNodeById }
     * 
     */
    public GetNodeById createGetNodeById() {
        return new GetNodeById();
    }

    /**
     * Create an instance of {@link GetRecordsByInterval }
     * 
     */
    public GetRecordsByInterval createGetRecordsByInterval() {
        return new GetRecordsByInterval();
    }

    /**
     * Create an instance of {@link GetRecordsAfter }
     * 
     */
    public GetRecordsAfter createGetRecordsAfter() {
        return new GetRecordsAfter();
    }

    /**
     * Create an instance of {@link GetHistory }
     * 
     */
    public GetHistory createGetHistory() {
        return new GetHistory();
    }

    /**
     * Create an instance of {@link GetRecordsAfterResponse }
     * 
     */
    public GetRecordsAfterResponse createGetRecordsAfterResponse() {
        return new GetRecordsAfterResponse();
    }

    /**
     * Create an instance of {@link GetRecordsCountResponse }
     * 
     */
    public GetRecordsCountResponse createGetRecordsCountResponse() {
        return new GetRecordsCountResponse();
    }

    /**
     * Create an instance of {@link GetRecordsByParamsResponse }
     * 
     */
    public GetRecordsByParamsResponse createGetRecordsByParamsResponse() {
        return new GetRecordsByParamsResponse();
    }

    /**
     * Create an instance of {@link SaveResponse }
     * 
     */
    public SaveResponse createSaveResponse() {
        return new SaveResponse();
    }

    /**
     * Create an instance of {@link GetRecordsByParams }
     * 
     */
    public GetRecordsByParams createGetRecordsByParams() {
        return new GetRecordsByParams();
    }

    /**
     * Create an instance of {@link GetHistoryByCategoryResponse }
     * 
     */
    public GetHistoryByCategoryResponse createGetHistoryByCategoryResponse() {
        return new GetHistoryByCategoryResponse();
    }

    /**
     * Create an instance of {@link GetRecordsResponse }
     * 
     */
    public GetRecordsResponse createGetRecordsResponse() {
        return new GetRecordsResponse();
    }

    /**
     * Create an instance of {@link GetHistoryResponse }
     * 
     */
    public GetHistoryResponse createGetHistoryResponse() {
        return new GetHistoryResponse();
    }

    /**
     * Create an instance of {@link MoveRecordToArchiveResponse }
     * 
     */
    public MoveRecordToArchiveResponse createMoveRecordToArchiveResponse() {
        return new MoveRecordToArchiveResponse();
    }

    /**
     * Create an instance of {@link GetHistoryByCategory }
     * 
     */
    public GetHistoryByCategory createGetHistoryByCategory() {
        return new GetHistoryByCategory();
    }

    /**
     * Create an instance of {@link GetLastRecords }
     * 
     */
    public GetLastRecords createGetLastRecords() {
        return new GetLastRecords();
    }

    /**
     * Create an instance of {@link GetNodeByIdResponse }
     * 
     */
    public GetNodeByIdResponse createGetNodeByIdResponse() {
        return new GetNodeByIdResponse();
    }

    /**
     * Create an instance of {@link MoveRecordToArchive }
     * 
     */
    public MoveRecordToArchive createMoveRecordToArchive() {
        return new MoveRecordToArchive();
    }

    /**
     * Create an instance of {@link BusinessJournalStoreRecord }
     * 
     */
    public BusinessJournalStoreRecord createBusinessJournalStoreRecord() {
        return new BusinessJournalStoreRecord();
    }

    /**
     * Create an instance of {@link GetRecords.Filter.Entry }
     * 
     */
    public GetRecords.Filter.Entry createGetRecordsFilterEntry() {
        return new GetRecords.Filter.Entry();
    }

    /**
     * Create an instance of {@link GetRecordsCount.Filter.Entry }
     * 
     */
    public GetRecordsCount.Filter.Entry createGetRecordsCountFilterEntry() {
        return new GetRecordsCount.Filter.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLastRecordsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getLastRecordsResponse")
    public JAXBElement<GetLastRecordsResponse> createGetLastRecordsResponse(GetLastRecordsResponse value) {
        return new JAXBElement<GetLastRecordsResponse>(_GetLastRecordsResponse_QNAME, GetLastRecordsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Save }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "save")
    public JAXBElement<Save> createSave(Save value) {
        return new JAXBElement<Save>(_Save_QNAME, Save.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsByInterval }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsByInterval")
    public JAXBElement<GetRecordsByInterval> createGetRecordsByInterval(GetRecordsByInterval value) {
        return new JAXBElement<GetRecordsByInterval>(_GetRecordsByInterval_QNAME, GetRecordsByInterval.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNodeById }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getNodeById")
    public JAXBElement<GetNodeById> createGetNodeById(GetNodeById value) {
        return new JAXBElement<GetNodeById>(_GetNodeById_QNAME, GetNodeById.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsByIntervalResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsByIntervalResponse")
    public JAXBElement<GetRecordsByIntervalResponse> createGetRecordsByIntervalResponse(GetRecordsByIntervalResponse value) {
        return new JAXBElement<GetRecordsByIntervalResponse>(_GetRecordsByIntervalResponse_QNAME, GetRecordsByIntervalResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsAfterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsAfterResponse")
    public JAXBElement<GetRecordsAfterResponse> createGetRecordsAfterResponse(GetRecordsAfterResponse value) {
        return new JAXBElement<GetRecordsAfterResponse>(_GetRecordsAfterResponse_QNAME, GetRecordsAfterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getHistory")
    public JAXBElement<GetHistory> createGetHistory(GetHistory value) {
        return new JAXBElement<GetHistory>(_GetHistory_QNAME, GetHistory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsAfter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsAfter")
    public JAXBElement<GetRecordsAfter> createGetRecordsAfter(GetRecordsAfter value) {
        return new JAXBElement<GetRecordsAfter>(_GetRecordsAfter_QNAME, GetRecordsAfter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsCount")
    public JAXBElement<GetRecordsCount> createGetRecordsCount(GetRecordsCount value) {
        return new JAXBElement<GetRecordsCount>(_GetRecordsCount_QNAME, GetRecordsCount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsByParamsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsByParamsResponse")
    public JAXBElement<GetRecordsByParamsResponse> createGetRecordsByParamsResponse(GetRecordsByParamsResponse value) {
        return new JAXBElement<GetRecordsByParamsResponse>(_GetRecordsByParamsResponse_QNAME, GetRecordsByParamsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsCountResponse")
    public JAXBElement<GetRecordsCountResponse> createGetRecordsCountResponse(GetRecordsCountResponse value) {
        return new JAXBElement<GetRecordsCountResponse>(_GetRecordsCountResponse_QNAME, GetRecordsCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecords }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecords")
    public JAXBElement<GetRecords> createGetRecords(GetRecords value) {
        return new JAXBElement<GetRecords>(_GetRecords_QNAME, GetRecords.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getHistoryResponse")
    public JAXBElement<GetHistoryResponse> createGetHistoryResponse(GetHistoryResponse value) {
        return new JAXBElement<GetHistoryResponse>(_GetHistoryResponse_QNAME, GetHistoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsResponse")
    public JAXBElement<GetRecordsResponse> createGetRecordsResponse(GetRecordsResponse value) {
        return new JAXBElement<GetRecordsResponse>(_GetRecordsResponse_QNAME, GetRecordsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistoryByCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getHistoryByCategoryResponse")
    public JAXBElement<GetHistoryByCategoryResponse> createGetHistoryByCategoryResponse(GetHistoryByCategoryResponse value) {
        return new JAXBElement<GetHistoryByCategoryResponse>(_GetHistoryByCategoryResponse_QNAME, GetHistoryByCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsByParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getRecordsByParams")
    public JAXBElement<GetRecordsByParams> createGetRecordsByParams(GetRecordsByParams value) {
        return new JAXBElement<GetRecordsByParams>(_GetRecordsByParams_QNAME, GetRecordsByParams.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "saveResponse")
    public JAXBElement<SaveResponse> createSaveResponse(SaveResponse value) {
        return new JAXBElement<SaveResponse>(_SaveResponse_QNAME, SaveResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MoveRecordToArchive }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "moveRecordToArchive")
    public JAXBElement<MoveRecordToArchive> createMoveRecordToArchive(MoveRecordToArchive value) {
        return new JAXBElement<MoveRecordToArchive>(_MoveRecordToArchive_QNAME, MoveRecordToArchive.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNodeByIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getNodeByIdResponse")
    public JAXBElement<GetNodeByIdResponse> createGetNodeByIdResponse(GetNodeByIdResponse value) {
        return new JAXBElement<GetNodeByIdResponse>(_GetNodeByIdResponse_QNAME, GetNodeByIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLastRecords }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getLastRecords")
    public JAXBElement<GetLastRecords> createGetLastRecords(GetLastRecords value) {
        return new JAXBElement<GetLastRecords>(_GetLastRecords_QNAME, GetLastRecords.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistoryByCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "getHistoryByCategory")
    public JAXBElement<GetHistoryByCategory> createGetHistoryByCategory(GetHistoryByCategory value) {
        return new JAXBElement<GetHistoryByCategory>(_GetHistoryByCategory_QNAME, GetHistoryByCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MoveRecordToArchiveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://remote.businessjournal.lecm.it.ru/", name = "moveRecordToArchiveResponse")
    public JAXBElement<MoveRecordToArchiveResponse> createMoveRecordToArchiveResponse(MoveRecordToArchiveResponse value) {
        return new JAXBElement<MoveRecordToArchiveResponse>(_MoveRecordToArchiveResponse_QNAME, MoveRecordToArchiveResponse.class, null, value);
    }

}
