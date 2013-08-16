package ru.it.lecm.reports.model.impl;

import java.io.Serializable;

import ru.it.lecm.reports.api.model.AlfrescoAssocInfo;

public class AlfrescoAssocInfoImpl implements AlfrescoAssocInfo, Serializable {

	private static final long serialVersionUID = 1L;

	private String assocTypeName;
	private AssocKind assocKind;

	@Override
	public String getAssocTypeName() {
		return assocTypeName;
	}

	@Override
	public void setAssocTypeName(String assocTypeName) {
		this.assocTypeName = assocTypeName;
	}

	@Override
	public AssocKind getAssocKind() {
		return assocKind;
	}

	@Override
	public void setAssocKind(AssocKind assocKind) {
		this.assocKind = assocKind;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assocKind == null) ? 0 : assocKind.hashCode());
		result = prime * result
				+ ((assocTypeName == null) ? 0 : assocTypeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlfrescoAssocInfoImpl other = (AlfrescoAssocInfoImpl) obj;
		if (assocKind != other.assocKind)
			return false;
		if (assocTypeName == null) {
			if (other.assocTypeName != null)
				return false;
		} else if (!assocTypeName.equals(other.assocTypeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("AlfrescoAssoc [");
		builder.append("assocTypeName=").append(assocTypeName);
		builder.append(", assocKind=").append(assocKind);
		builder.append("]");
		return builder.toString();
	}

}
