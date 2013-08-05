package ru.unicloud.gate;

import com.microsoft.schemas.x2003.x10.serialization.arrays.ArrayOfbase64Binary;
import java.util.List;
import javax.xml.ws.Holder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocflowInfoBase;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.ArrayOfDocumentInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.CompanyInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocflowInfoBase;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentInfo;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.DocumentToSend;
import org.datacontract.schemas.x2004.x07.uCloudGateProxy.WorkspaceFilter;
import org.datacontract.schemas.x2004.x07.uCloudGateProxyExceptions.GateResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * "Неформализованный" документооборот между Маратов Марат Маратович и Андреев Андрей Андреевич
 * @author VLadimir Malygin
 * @since 19.06.2013 10:42:13
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class SendReceiveDocumentTest extends GateWcfServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(SendReceiveDocumentTest.class);

	/**
	 * отправить подписанный документ от контрагента Маратов Марат Маратович к контрагенту Андреев Андрей Андреевич
	 * документ подписан с использованием локального сертификата
	 * @throws Exception
	 * @return ИДшник документа который был выслан
	 */
	private String sendDocumentFrom700000008094To700000016017() throws Exception {
		logger.info("authenticateByCertificate for Маратов Марат Маратович");
		ClassPathResource resource = new ClassPathResource("/AuthenticateByCertificate/timestamp-700000008094.sign");
		String base64Sign = IOUtils.toString(resource.getInputStream());
		byte signature[] = Base64.decodeBase64(base64Sign);
		String signedData = IOUtils.toString(new ClassPathResource("/AuthenticateByCertificate/timestamp.txt").getInputStream());
        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
        Holder<String> token = new Holder<String>();
		service.authenticateByCertificate(OPERATOR_CODE, signature, signedData, gateResponse, token);
		logger.info("Маратов Марат Маратович received the token {}", token.value);

		setAuthHeaders(PARTNER_KEY, INN_700000008094, "12a218ce-2245-4498-ae73-ad655ac07749", OPERATOR_CODE, token.value);

		CompanyInfo companyInfo = CompanyInfo.Factory.newInstance();
		companyInfo.setInn("700000016017");
		companyInfo.setKpp(null);

		ArrayOfbase64Binary signatures = ArrayOfbase64Binary.Factory.newInstance();
		signatures.addBase64Binary(IOUtils.toByteArray(new ClassPathResource("/SendDocument/document-700000008094.sign").getInputStream()));

		DocumentToSend doc = DocumentToSend.Factory.newInstance();
		doc.setContent(IOUtils.toByteArray(new ClassPathResource("/SendDocument/document.pdf").getInputStream()));
		doc.setDocumentType(org.datacontract.schemas.x2004.x07.uCloudGateProxy.EDocumentType.Enum.forString("NonFormalized"));
		doc.setFileName("document.pdf");
		doc.setId("AB2E2CD3-9654-4608-950C-581D5985B364");
		doc.setReceiver(companyInfo);
		doc.setSignatures(signatures);
		doc.setTransactionType(org.datacontract.schemas.x2004.x07.uCloudGateProxyDocflow.EDocflowTransactionType.Enum.forString("NoRecipientSignatureRequest"));
		gateResponse = new Holder<GateResponse>();
		Holder<String> documentId = new Holder<String>();
		service.sendDocument(doc, OPERATOR_CODE, null, gateResponse, documentId);
		logger.info("Маратов Марат Маратович has sent document for Андреев Андрей Андреевич. documentId = {}", documentId.value);
		logGateResponce(logger, gateResponse);
		return documentId.value;
	}

	/**
	 * контрагент Андреев Андрей Андреевич пытается найти в общем перечне документов тот, который его интересует
	 */
	private void getDocumentsListBy700000016017() throws Exception {
		logger.info("authenticateByCertificate for Андреев Андрей Андреевич");
		ClassPathResource resource = new ClassPathResource("/AuthenticateByCertificate/timestamp-700000016017.sign");
		String base64Sign = IOUtils.toString(resource.getInputStream());
		byte signature[] = Base64.decodeBase64(base64Sign);
		String signedData = IOUtils.toString(new ClassPathResource("/AuthenticateByCertificate/timestamp.txt").getInputStream());
        Holder<GateResponse> gateResponse = new Holder<GateResponse>();
        Holder<String> token = new Holder<String>();
		service.authenticateByCertificate(OPERATOR_CODE, signature, signedData, gateResponse, token);
		logger.info("Андреев Андрей Андреевич received the token {}", token.value);

		setAuthHeaders(PARTNER_KEY, INN_700000016017, "94a48215-3cc3-4242-8f9c-55b41edfa25e", OPERATOR_CODE, token.value);

		WorkspaceFilter filter = WorkspaceFilter.Factory.newInstance();
		filter.setRelation(org.datacontract.schemas.x2004.x07.uCloudGateProxy.ERelationFilter.Enum.forString("Inbound"));
        gateResponse = new Holder<GateResponse>();
		Holder<ArrayOfDocflowInfoBase> docflows = new Holder<ArrayOfDocflowInfoBase>();
		service.getDocflowList(filter, gateResponse, docflows);
		logGateResponce(logger, gateResponse);
		if (docflows.value != null) {
			List<DocflowInfoBase> docflowInfoBaseList = docflows.value.getDocflowInfoBaseList();
			for (DocflowInfoBase docflowInfoBase : docflowInfoBaseList) {
				logger.info("docflowId = {}, sender = {}, receiver = {}, type = {}, description = {}",
							new Object[] {
							docflowInfoBase.getDocflowId(),
							docflowInfoBase.getSender().getInn(),
							docflowInfoBase.getReceiver().getInn(),
							docflowInfoBase.getType().toString(),
							docflowInfoBase.getDescription()});

		        gateResponse = new Holder<GateResponse>();
				Holder<ArrayOfDocumentInfo> documentInfos = new Holder<ArrayOfDocumentInfo>();
				service.getDocumentList(docflowInfoBase.getDocflowId(), gateResponse, documentInfos);
				if (documentInfos != null) {
					List<DocumentInfo> documentInfoList = documentInfos.value.getDocumentInfoList();
					for (DocumentInfo documentInfo : documentInfoList) {
						logger.info("\tdocumentId = {}, documentType = {}, filename = {}, transactionType = {}",
									new Object[] {
									documentInfo.getDocumentId(),
									documentInfo.getDocumentType().toString(),
									documentInfo.getFileName(),
									documentInfo.getTransactionType().toString()});
					}
				}
			}
		}
	}

	/**
	 * отправить подписанный документ от контрагента Маратов Марат Маратович к контрагенту Андреев Андрей Андреевич
	 * документ подписан с использованием локального сертификата
	 * @throws Exception
	 */
	@Test
	public void sendDocument() throws Exception {
		String documentId = sendDocumentFrom700000008094To700000016017();
		getDocumentsListBy700000016017();
	}
}
