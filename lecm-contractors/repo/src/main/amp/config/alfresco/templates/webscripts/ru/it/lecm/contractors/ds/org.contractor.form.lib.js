/* global logger, search, orgstructure */

function createLinkedOrganization(persistedObject) {
	try {
		var contractor = search.findNode(persistedObject);
		var holding = orgstructure.getHolding();
		var orgUnit = holding.createNode(contractor.name, 'lecm-orgstr:organization-unit', {
			'lecm-orgstr:element-full-name': contractor.properties['lecm-contractor:fullname'],
			'lecm-orgstr:element-short-name': contractor.properties['lecm-contractor:shortname'],
			'lecm-orgstr:unit-code': contractor.properties['lecm-contractor:contractor-code'],
			'lecm-orgstr:unit-type': 'SEGREGATED'
		});
		contractor.addAspect('lecm-orgstr-aspects:is-organization-aspect');
		orgUnit.addAspect('lecm-orgstr-aspects:has-linked-organization-aspect');
		orgUnit.createAssociation(contractor, 'lecm-orgstr-aspects:linked-organization-assoc');
	} catch (error) {
		var msg = error.message;
		status.setCode(500, msg);

		if (logger.isLoggingEnabled()) {
			logger.log(msg);
			logger.log("Returning 500 status code");
		}
	}
}
