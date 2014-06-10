package ru.it.lecm.integrotest.actions;



/**
 * Непосредственное действие для создания Департамента
 * @author rabdullin
 */
public class CreateOrgUnitAction extends CreateNode {

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

/*
	@Override
	protected void checkArgs(NodeRef parentRef, QName assocTypeQName, QName assocQName,
			QName nodeTypeQName, Map<QName, Serializable> data) {
		// NOTE: если надо жёсткость, то здесь можно проверить что тип деёствительно OU... 
	}
 */

	public void run() {
		logger.info( String.format("... creating OU-node: name '%s' under parent '%s'", orgNameCode, orgParentByNameCode));

		// добавление в начальные свойства названия нового подразделения
		addProp( "lecm-orgstr:unit-code", this.orgNameCode);

		// если не указан тип - укажем его как организация ...
		if (super.getNodeType() == null)
			setNodeType( "lecm-orgstr:organization-unit" ); // OrgstructureBean.TYPE_ORGANIZATION_UNIT.getLocalName());

		super.run();

		// getContext().getOrgstructureService()...
	}

}
