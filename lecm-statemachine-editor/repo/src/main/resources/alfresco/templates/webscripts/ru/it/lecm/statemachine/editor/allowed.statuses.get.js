var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var dictionaryService = ctx.getBean("DictionaryService");
var qname = Packages.org.alfresco.service.namespace.QName;
var constraintQname = qname.createQName("http://www.it.ru/logicECM/statemachine/1.0", "statuses");
var defConstraint = dictionaryService.getConstraint(constraintQname);
var constraint = defConstraint.getConstraint();

var allowedValues = constraint.getAllowedValues().toArray();

var statuses = [];
for each (var status in allowedValues) {
	statuses.push({
		name: status,
		label: constraint.getDisplayLabel(status)
	});
}

model.statuses = statuses;
