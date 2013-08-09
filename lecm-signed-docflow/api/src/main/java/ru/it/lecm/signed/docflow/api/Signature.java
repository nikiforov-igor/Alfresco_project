package ru.it.lecm.signed.docflow.api;

import java.util.Date;

public class Signature {

	private String nodeRef;

	private String signedContentName;

	private String owner;
	private String ownerPosition;
	private String ownerOrganization;
	private String serialNumber;
	private String ca;

	private Boolean isValid;
	private Boolean isOur;

	private Date signingDate;
	private String signingDateString;

	private Date validFrom;
	private String validFromString;

	private Date validThrough;
	private String validThroughString;

	private Date updateDate;
	private String updateDateString;

	private String signatureContent;
	private String fingerprint;

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String getSignedContentName() {
		return signedContentName;
	}

	public void setSignedContentName(String signedContentName) {
		this.signedContentName = signedContentName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerPosition() {
		return ownerPosition;
	}

	public void setOwnerPosition(String ownerPosition) {
		this.ownerPosition = ownerPosition;
	}

	public String getOwnerOrganization() {
		return ownerOrganization;
	}

	public void setOwnerOrganization(String ownerOrganization) {
		this.ownerOrganization = ownerOrganization;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getCa() {
		return ca;
	}

	public void setCa(String ca) {
		this.ca = ca;
	}

	public Boolean getValid() {
		return isValid;
	}

	public void setValid(Boolean valid) {
		isValid = valid;
	}

	public Boolean getOur() {
		return isOur;
	}

	public void setOur(Boolean our) {
		isOur = our;
	}

	public Date getSigningDate() {
		return signingDate;
	}

	public void setSigningDate(Date signingDate) {
		this.signingDate = signingDate;
	}

	public String getSigningDateString() {
		return signingDateString;
	}

	public void setSigningDateString(String signingDateString) {
		this.signingDateString = signingDateString;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public String getValidFromString() {
		return validFromString;
	}

	public void setValidFromString(String validFromString) {
		this.validFromString = validFromString;
	}

	public Date getValidThrough() {
		return validThrough;
	}

	public void setValidThrough(Date validThrough) {
		this.validThrough = validThrough;
	}

	public String getValidThroughString() {
		return validThroughString;
	}

	public void setValidThroughString(String validThroughString) {
		this.validThroughString = validThroughString;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateDateString() {
		return updateDateString;
	}

	public void setUpdateDateString(String updateDateString) {
		this.updateDateString = updateDateString;
	}

	public String getSignatureContent() {
		return signatureContent;
	}

	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
}