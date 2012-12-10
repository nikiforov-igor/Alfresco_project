package ru.it.lecm.security.impl;

import org.alfresco.error.AlfrescoRuntimeException;

public class OrgStructureVoterException extends AlfrescoRuntimeException {

	private static final long serialVersionUID = 1L;

	public OrgStructureVoterException(String msg)
	{
		super(msg);
	}

	public OrgStructureVoterException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

}
