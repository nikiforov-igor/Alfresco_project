(function () {
	if (typeof Evaluator == "undefined" || !Evaluator) {
		Evaluator = {};
	}

	Evaluator.run = function (node, fields, nameSubstituteStrings, ctx) {
        var permissions = {},
	        createdBy = Common.getPerson(node.properties["cm:creator"]),
	        modifiedBy = Common.getPerson(node.properties["cm:modifier"]),
            nodeData = {};

        /**
         * PERMISSIONS
         */
		permissions = {
            "create": node.hasPermission("CreateChildren"),
            "edit": node.hasPermission("Write"),
            "delete": ((review.deleteRowAllowed(node)==true) && node.hasPermission("Delete"))
        };

        for (var i = 0; i < fields.length; i++) {
            //fields[i] = напримери, cm_name
            var fName = fields[i];
            var realFieldName = fields[i].replace("_", ":");

            var nameSubstituteStringDef = null;
            if (nameSubstituteStrings != null && nameSubstituteStrings[i] != null && nameSubstituteStrings[i] != "") {
                nameSubstituteStringDef = nameSubstituteStrings[i];
            }

            var fieldData;

            // вытащить дефинишены
            var propDefinition = null, assocDefinition = null;

            propDefinition = base.getProperty(realFieldName);
            if (propDefinition == null) {
                assocDefinition = base.getAssociation(realFieldName);
            }

            if (propDefinition != null || assocDefinition != null) {
                var isAssoc = assocDefinition != null,
                    type = isAssoc ?
                        base.qNameToPrefixString(assocDefinition.getTargetClass().getName()) :
                        base.qNameToPrefixString(propDefinition.getDataType().getName()),
                    endpointMany = isAssoc ? assocDefinition.isTargetMany() : false;

                if (!isAssoc && type.indexOf("d:") == 0) {
                    type = type.substring("d:".length);
                }

                fieldData = {
                    type: type
                };

                var value = null;
                if (!isAssoc) {
                    value = node.properties[realFieldName];
                    if (value != null && propDefinition.getDataType().getJavaClassName() == "java.util.Date") {
                        value = utils.toISO8601(value);
                    }
                } else {
                    var associationNodes = node.assocs[realFieldName];
                    if (associationNodes != null) {
                        value = "";
                        for (var ii = 0; ii < associationNodes.length; ii++) {
                            value = value + associationNodes[ii].nodeRef.toString() + ",";
                        }
                        value = value.length > 0 ? value.substring(0, value.length - 1) : "";
                    }
                }

                if (endpointMany) {
                    if (value != null && value.length > 0) {
                        var values = value.split(",");
                        nodeData["assoc_" + fName] = [];
                        for each(value in values) {
                            var objLoop = {
                                type: fieldData.type,
                                value: value,
                                displayValue: value
                            };

                            if (Evaluator.decorateFieldData(objLoop, node, nameSubstituteStringDef)) {
                                nodeData["assoc_" + fName].push(objLoop);
                            }
                        }
                    }
                } else {
                    fieldData.value = value;
                    fieldData.displayValue =  isAssoc ? value : Evaluator.translateField(propDefinition, value);

                    if (Evaluator.decorateFieldData(fieldData, node, nameSubstituteStringDef)) {
                        nodeData[(isAssoc ? "assoc_" : "prop_") + fName] = fieldData;
                    }
                }
            } else { // работает сразу через substitute сервис
                if (fName == "mimetype") {
                    nodeData["prop_" + fName] = {
                        type: "text",
                        value: node.getMimetype(),
                        displayValue: node.getMimetype()
                    }; // prop_mimetype
                } else if (fName == "size") {
                    nodeData["prop_" + fName] = {
                        type: "size",
                        value: node.getSize(),
                        displayValue: node.getSize()
                    }; // prop_size
                } else if (fName == "creator") {
                    var login = node.properties["cm:creator"];
                    var person = people.getPerson(login);
                    if (person != null) {
                        var personData = Evaluator.getPersonObject(person.nodeRef.toString());
                        nodeData["prop_" + fName] = {
                            type: "size",
                            value: personData.displayName,
                            displayValue: personData.displayName
                        }; // prop_creator
                    }
                } else {
                    fieldData = {
                        type: "text"
                    };
                    if (Evaluator.decorateFieldData(fieldData, node, nameSubstituteStringDef)) {
                        nodeData["prop_" + fName] = fieldData; // prop_cm_name
                    }
                }
            }
        }


        return({
            node: node,
            nodeData: nodeData,
            actionPermissions: permissions,
	        createdBy: createdBy,
	        modifiedBy: modifiedBy,
            page: documentScript.getViewUrl(node)
        });
	};
})();
