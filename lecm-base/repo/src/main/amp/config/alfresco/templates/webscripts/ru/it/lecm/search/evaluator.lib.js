/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

var Evaluator = {
	/**
	 * Cache for cm:person objects
	 */
	PeopleObjectCache: {},

    /**
     * Gets / caches a person object
     *
     * @method getPersonObject
     * @param nodeRef {string} NodeRef of a cm:person object
     * @return {null}
     */
	getPersonObject: function Evaluator_getPersonObject(nodeRef) {
		if (nodeRef == null || nodeRef == "") {
			return null;
		}

		if (typeof Evaluator.PeopleObjectCache[nodeRef] == "undefined") {
			var person = search.findNode(nodeRef);
			Evaluator.PeopleObjectCache[nodeRef] =
			{
				userName: person.properties.userName,
				firstName: person.properties.firstName,
				lastName: person.properties.lastName,
				displayName: (person.properties.firstName + " " + person.properties.lastName).replace(/^\s+|\s+$/g, "")
			};
			if (person.assocs["cm:avatar"] != null) {
				Evaluator.PeopleObjectCache[nodeRef].avatar = person.assocs["cm:avatar"][0];
			}
		}
		return Evaluator.PeopleObjectCache[nodeRef];
	},

	/**
	 * Cache for nodes that are subtypes of cm:cmobject
	 */
	ContentObjectCache: {},

    /**
     * Gets / caches a content object
     *
     * @method getContentObject
     * @param nodeRef {string} NodeRef
     * @return {null}
     */
	getContentObject: function Evaluator_getContentObject(nodeRef) {
		if (nodeRef == null || nodeRef == "") {
			return null;
		}

		if (typeof Evaluator.ContentObjectCache[nodeRef] == "undefined") {
			var node = search.findNode(nodeRef);
			try {
				Evaluator.ContentObjectCache[nodeRef] = node;
			}
			catch (e) {
				// Possibly a stale indexed node
				return null;
			}
		}
		return Evaluator.ContentObjectCache[nodeRef];
	},

    /**
     * Generate displayValue and any extra metadata for this field
     *
     * @method decorateFieldData
     * @param objData {object} Object literal containing this field's data
     * @param node {ScriptNode} The list item node for this field
     * @return {Boolean} false to prevent this field being added to the output stream.
     * @param nameSubstituteString
     */
    decorateFieldData: function Evaluator_decorateFieldData(objData, node, nameSubstituteString) {
        var value = objData.value,
            type = objData.type,
            obj;

        if (value == null && nameSubstituteString == null) {
            return false;
        }

        var substituteNode = null;
        var includeRef = false;
        if (nameSubstituteString != null && nameSubstituteString.indexOf("$parent") == 0) {
            nameSubstituteString = nameSubstituteString.replace("$parent", "");
            substituteNode = node;
        }
        if (nameSubstituteString != null && nameSubstituteString.indexOf("$includeRef") == 0) {
            nameSubstituteString = nameSubstituteString.replace("$includeRef", "");
            includeRef = true;
        }
        if (type == "cm:person") {
            obj = Evaluator.getPersonObject(value);
            if (obj == null) {
                return false;
            }
            if (nameSubstituteString == null) {
                objData.displayValue = obj.displayName;
            } else {
                objData.displayValue = substitude.formatNodeTitle(value, nameSubstituteString);
            }
            objData.metadata = obj.userName;
        }
        else if (type == "cm:folder") {
            obj = Evaluator.getContentObject(value);
            if (obj == null) {
                return false;
            }
            objData.displayValue = obj.displayPath.substring(companyhome.name.length() + 1);
            objData.metadata = "container";
        }
        else if (type.indexOf(":") > 0 && node.isSubType("cm:cmobject")) {
            obj = Evaluator.getContentObject(value);
            if (obj == null || obj.nodeRef.toString().startsWith("archive")) {
                return false;
            }
            if (substituteNode == null) {
                substituteNode = obj;
            }
            if (nameSubstituteString == null) {
                objData.displayValue = substitude.getObjectDescription(obj);
            } else {
                objData.displayValue = substitude.formatNodeTitle(substituteNode, nameSubstituteString);
            }
            objData.metadata = obj.isContainer ? "container" : "document";
        } else if (nameSubstituteString != null) {
            objData.displayValue = substitude.formatNodeTitle(node, nameSubstituteString);
            if (!includeRef) {
                objData.value = objData.displayValue;
            } else {
                var refs = substitude.getObjectsByTitle(node, nameSubstituteString);
                objData.value = refs.length > 0 ? refs[0].nodeRef.toString() : node.nodeRef.toString();
            }
        }
        return true;
    },

    /**
     * Translates a List fieldDefinition
     *
     * @method translateField
     * @param propertyDef {PropertyDefinition} objDef
     * @param value {String} default value
     * @return {String}
     */
	
	translateField: function Evaluator_translateField(objDef, value) {
		if (!objDef) {
			return null;
		}

		if (objDef.constraints) {
			for (var i = 0, len = objDef.constraints.size(); i < len; ++i) {
                var constraint = objDef.constraints.get(i).constraint;
				if ("LIST" == constraint.type) {
					var allowedV = constraint.allowedValues;
					for (var j = 0; j < allowedV.size(); ++j) {
						var allowedValue = "" + allowedV.get(j);
                        if (value == allowedValue) {
                            return base.getListConstraintDisplayValue(constraint, allowedValue);
                        }
					}
				}
			}
		}
		return value;
	},

	/**
	 * Node Evaluator - main entrypoint
	 */
    run: function Evaluator_run(node, fields, nameSubstituteStrings) {
        var permissions = {},
	        createdBy = Common.getPerson(node.properties["cm:creator"]),
	        modifiedBy = Common.getPerson(node.properties["cm:modifier"]),
            nodeData = {};

        /**
         * PERMISSIONS
         */
        var deletePermission = node.hasPermission("Delete");
//        т.к. удаление из грида для документов больше не используем - дополнительные проверки не требуются
//        if (statemachine.hasStatemachine(node)) {
//            deletePermission = deletePermission && statemachine.isDraft(node);
//        }

        permissions = {
            "create": node.hasPermission("CreateChildren"),
            "edit": node.hasPermission("Write"),
            "delete": deletePermission
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

			///////////// PropertyDefinition
            propDefinition = base.getProperty(realFieldName);
            if (propDefinition == null) {
            ///////////// assocDefinition
                assocDefinition = base.getAssociation(realFieldName);
            }

            if (propDefinition != null || assocDefinition != null) {
                var isAssoc = assocDefinition != null,
                    type = isAssoc ?
                        base.toPrefixString(assocDefinition) :
                        base.toPrefixString(propDefinition),
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
    }
};
