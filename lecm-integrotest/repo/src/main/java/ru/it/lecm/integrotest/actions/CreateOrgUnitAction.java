package ru.it.lecm.integrotest.actions;


/**
 * Непосредственное действие для создания Департамента
 * @author rabdullin
 */
public class CreateOrgUnitAction extends LecmActionBase {

	/**
	 * The node name.
	 * Example: "O1_1"
	 */
	private String orgNameCode;
	private String orgParentByNameCode;

	public CreateOrgUnitAction() {
		super();
		logger.info( this.getClass().getCanonicalName() + " created");
	}

	public String getOrgNameCode() {
		return orgNameCode;
	}

	public void setOrgNameCode(String value) {
		this.orgNameCode = value;
	}

	public String getOrgParentByNameCode() {
		return orgParentByNameCode;
	}

	public void setOrgParentByNameCode(String value) {
		this.orgParentByNameCode = value;
	}

	@Override
	public void run() {
		logger.info( String.format("... creating OU-node: name ''%s' under parent''%s'", orgNameCode, orgParentByNameCode));
		// getContext().getOrgstructureService().createStaff(orgElement, staffPosition)
		// TestFailException
	}

}
