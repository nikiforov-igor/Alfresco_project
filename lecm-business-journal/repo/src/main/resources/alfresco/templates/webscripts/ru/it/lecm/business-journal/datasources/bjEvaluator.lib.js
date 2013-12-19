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

var BJEvaluator =
{
    /**
     * Cache for cm:person objects
     */
    PeopleObjectCache: {},

    /**
     * Gets / caches a person object
     *
     * @method getPersonObject
     * @param nodeRef {string} NodeRef of a cm:person object
     */
    getPersonObject: function Evaluator_getPersonObject(nodeRef)
    {
        if (nodeRef == null || nodeRef == "")
        {
            return null;
        }

        if (typeof BJEvaluator.PeopleObjectCache[nodeRef] == "undefined")
        {
            var person = search.findNode(nodeRef);
            BJEvaluator.PeopleObjectCache[nodeRef] =
            {
                userName: person.properties.userName,
                firstName: person.properties.firstName,
                lastName: person.properties.lastName,
                displayName: (person.properties.firstName + " " + person.properties.lastName).replace(/^\s+|\s+$/g, "")
            };
            if (person.assocs["cm:avatar"] != null)
            {
                BJEvaluator.PeopleObjectCache[nodeRef].avatar = person.assocs["cm:avatar"][0];
            }
        }
        return BJEvaluator.PeopleObjectCache[nodeRef];
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
     */
    getContentObject: function Evaluator_getContentObject(nodeRef)
    {
        if (nodeRef == null || nodeRef == "")
        {
            return null;
        }

        if (typeof BJEvaluator.ContentObjectCache[nodeRef] == "undefined")
        {
            var node = search.findNode(nodeRef);
            try
            {
                BJEvaluator.ContentObjectCache[nodeRef] = node;
            }
            catch(e)
            {
                // Possibly a stale indexed node
                return null;
            }
        }
        return BJEvaluator.ContentObjectCache[nodeRef];
    },

    /**
     * Generate displayValue and any extra metadata for this field
     *
     * @method decorateFieldData
     * @param objData {object} Object literal containing this field's data
     * @param node {ScriptNode} The list item node for this field
     * @return {Boolean} false to prevent this field being added to the output stream.
     */
    decorateFieldData: function Evaluator_decorateFieldData(objData, node, nameSubstituteString) {
        var value = objData.value,
            type = objData.type,
            obj;
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
            obj = BJEvaluator.getPersonObject(value);
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
            obj = BJEvaluator.getContentObject(value);
            if (obj == null) {
                return false;
            }
            objData.displayValue = obj.displayPath.substring(companyhome.name.length() + 1);
            objData.metadata = "container";
        }
        else if (type.indexOf(":") > 0 && node.getMainObject().isSubType("cm:cmobject")) {
            obj = BJEvaluator.getContentObject(value);
            if (obj == null || obj.nodeRef.toString().startsWith("archive")) {
                return false;
            }
            if (substituteNode == null) {
                substituteNode = obj;
            }
            if (nameSubstituteString == null) {
                objData.displayValue = obj.properties["cm:name"];
            } else {
                objData.displayValue = substitude.formatNodeTitle(node.getMainObject(), nameSubstituteString);
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
     * @param objDef {FieldDefinition} objDef
     * @param value {String} default value
     */
    translateField: function Evaluator_translateField(objDef, value)
    {
        if (objDef == null || objDef == "")
        {
            return null;
        }
        if (objDef.constraints != null)
        {
            for ( var i=0, len= objDef.constraints.size(); i<len; ++i )
            {
                if ("LIST" == objDef.constraints.get(i).type)
                {
                    var allowedV = objDef.constraints.get(i).parameters.allowedValues;
                    for (var j=0; j<allowedV.size(); ++j )
                    {
                        var allowedVasString = "" + allowedV.get(j);
                        var allValSplit = allowedVasString.split("|");
                        if (value == allValSplit[0]) {
                            return allValSplit[1];
                        }
                    }
                }
            }
        }
        return value;
    },

    /**
     * Node BJEvaluator - main entrypoint
     */
    run: function BJEvaluator_run(record, fields, nameSubstituteStrings)
    {
        var permissions = {},
            actionSet = "",
            actionLabels = {},
            createdBy = record.getInitiator() != null ? Common.getPerson(record.getInitiator().properties["cm:creator"]) : Common.getPerson("System"),
            modifiedBy = record.getInitiator() != null ? Common.getPerson(record.getInitiator().properties["cm:modifier"]) : Common.getPerson("System"),
            nodeData = {};

        /**
         * PERMISSIONS
         */

        permissions =
        {
            "create": true,
            "edit": true,
            "delete": true
        };

        // Make sure we can quickly look-up the Field Definition within the formData loop...
        var objDefinitions = {};
        var nameSubstituteStringDefs = {};
        for (var i = 0; i < fields.length; i++) {
            var fName = fields[i].replace(":","_");
            if (nameSubstituteStrings != null && nameSubstituteStrings[i] != null && nameSubstituteStrings[i] != "") {
                nameSubstituteStringDefs[fName] = nameSubstituteStrings[i];
            }
        }

        // Populate the data model
        for (var k in fields)
        {
            if (fields[k] == "lecm-busjournal:bjRecord-date") {
                var type = "datetime";
                var value = utils.toISO8601(record.getDate());
                var displayValue = value;

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "prop_" + fields[k].replace(":","_");
                nodeData[key] = objData;

            } else if (fields[k] == "lecm-busjournal:bjRecord-description") {
                var type = "text";
                var value = record.getRecordDescription();
                var displayValue = record.getRecordDescription();

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }
                var key = "prop_" + fields[k].replace(":","_");
                nodeData[key] = objData;

            } else if (fields[k] == "lecm-busjournal:bjRecord-objType-assoc") {
                var type = "lecm-busjournal:objectType";
                var value = record.getObjectTypeText();
                var displayValue = record.getObjectTypeText();

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "assoc_" + fields[k].replace(":","_");
                nodeData[key] = objData;

            } else if (fields[k] == "lecm-busjournal:bjRecord-evCategory-assoc") {
                var type = "lecm-busjournal:eventCategory";
                var value = record.getEventCategoryText();
                var displayValue = record.getEventCategoryText();

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "assoc_" + fields[k].replace(":","_");
                nodeData[key] = objData;
            } else if (fields[k] == "lecm-busjournal:bjRecord-mainObject-assoc") {
                var type = "cm:cmobject";
	            var value = "";
	            if (record.getMainObject() != null && record.getMainObject().exists()) {
                    value = record.getMainObject().nodeRef.toString();
	            }
                var displayValue = record.getMainObjectDescription();

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "assoc_" + fields[k].replace(":","_");
                nodeData[key] = objData;
            } else if (fields[k] == "lecm-busjournal:secondary-objects") {
                var type = "text";
                var value = record.getObject1() + " " + record.getObject2() + " " + record.getObject3() + " " + record.getObject4() + " " + record.getObject5();
                var displayValue = value;

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "prop_" + fields[k].replace(":","_");
                nodeData[key] = objData;
            } else if (fields[k] == "lecm-busjournal:bjRecord-initiator-assoc") {
                var type = "lecm-orgstr:employee";
                var value = "";
                var displayValue = "";
                if (record.getInitiator() != null) {
                    value = record.getInitiator().nodeRef.toString();
                    displayValue = record.getInitiator().properties["cm:name"];
                }

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "assoc_" + fields[k].replace(":","_");
                nodeData[key] = objData;
            } else if (fields[k] == "cm:versionLabel") {
                var type = "";
                var value = "";
                var displayValue = value;

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "prop_" + fields[k].replace(":","_");
                nodeData[key] = objData;
            } else if (fields[k] == "lecm-dic:active") {
                var type = "boolean";
                var value = record.isActive();
                var displayValue = record.isActive();

                var objData = {
                    type: type,
                    value: value,
                    displayValue: displayValue
                }

                var key = "prop_" + fields[k].replace(":","_");
                nodeData[key] = objData;
            }

        }

        return(
        {
            node: {
                nodeRef: record.getNodeId().toString(),
                typeShort: "lecm-busjournal:bjRecord",
                properties: {
                    created: record.getDate(),
                    modified: record.getDate()
                }
            },
            nodeData: nodeData,
            actionSet: actionSet,
            actionPermissions: permissions,
            createdBy: createdBy,
            modifiedBy: modifiedBy,
            tags: [],
            actionLabels: actionLabels
        });
    }
};
