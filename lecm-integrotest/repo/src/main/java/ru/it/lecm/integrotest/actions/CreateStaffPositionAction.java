package ru.it.lecm.integrotest.actions;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.integrotest.FinderBean;


/**
 * Непосредственное действие для создания Департамента
 * @author rabdullin
 */
public class CreateStaffPositionAction extends LecmActionBase {

	/**
	 * The node name.
	 * Example: "O1_1"
	 */
	private String orgName, dpName, employeeName;
	private boolean isPrimary;


	public CreateStaffPositionAction() {
		super();
		logger.info( this.getClass().getCanonicalName() + " created");
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getDpName() {
		return dpName;
	}

	public void setDpName(String value) {
		this.dpName = value;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public void setEmployeeName(String value) {
		this.employeeName = value;
	}

	public boolean isPrimary() {
		return this.isPrimary;
	}

	public void setPrimary(boolean value) {
		this.isPrimary = value;
	}

	@Override
	public void run() {
		logger.info( String.format("... test creating staff position:\n\t orgUnit '%s'\n\t position '%s'\n\t employee %s", 
				this.orgName, this.dpName, this.employeeName));

		final FinderBean finder = getContext().getFinder();

		final NodeRef ouRef = finder.findOUByName( this.getOrgName());
		final NodeRef dpRef = finder.findDpByName( this.getDpName());
		final NodeRef userRef = finder.findEmployeeByName( this.getEmployeeName());

		final NodeRef newNode = getContext().getOrgstructureService().createStaff(ouRef, dpRef);
		getContext().getOrgstructureService().includeEmployeeIntoStaff(userRef, newNode, isPrimary());

		logger.info( String.format("staff position created as {%s}:\n\t orgUnit '%s'\n\t position '%s'\n\t employee %s",
				newNode, this.orgName, this.dpName, this.employeeName));
	}

}
