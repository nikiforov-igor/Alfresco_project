<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/action/action.lib.js">

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

/**
 * Delete multiple items action
 * @method DELETE
 */

/**
 * Entrypoint required by action.lib.js
 *
 * @method runAction
 * @param p_params {object} Object literal containing items array
 * @return {object|null} object representation of action results
 */
function runAction(p_params) {
    var results = [],
        items = p_params.items,
        item, result, nodeRef;

    // Must have array of items
    if (!items || items.length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "No items supplied in JSON body.");
        return [];
    }

    var full = args["full"] != null ? args["full"] : false;
    var trash = args["trash"] != null ? args["trash"] : true;
    // фикс, чтобы всегда работали со строками
    full = "" + full;
    for (item in items) {
        nodeRef = items[item];
        result =
        {
            nodeRef:nodeRef,
            action:"deleteItem",
            success:false
        };

        try {
            var itemNode = search.findNode(nodeRef);
            if (itemNode != null) {
                var deletePermission = itemNode.hasPermission("Delete");
                if (deletePermission) {
                    if (full == "false") {// пометить объект как неактивный
                        itemNode.properties["lecm-dic:active"] = false;
                        itemNode.save();
                        result.success = true;
                    } else {//реальное удаление объекта
                        var sAssocs;
                        sAssocs = itemNode.getSourceAssocs();
                        // удалить все ссылки на объект
                        for (key in sAssocs) {
                            var assocsList = sAssocs[key];
                            for (index in assocsList) {
                                var targetA = assocsList[index];
                                targetA.removeAssociation(itemNode, key);
                            }
                        }
                        if (trash == "false") {
                            itemNode.addAspect("sys:temporary");
                        }
                        result.success = itemNode.remove();
                    }
                }
            }
        }
        catch (e) {
            result.success = false;
        }

        results.push(result);
    }
    return results;
}

/* Bootstrap action script */
main();
