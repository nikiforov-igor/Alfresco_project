function getLECMGroups(parentNode, result) {
    var authorities = parentNode.getChildAssocsByType("cm:authorityContainer");
    for each(var authority in authorities) {
        if (authority.properties["cm:authorityName"].indexOf("GROUP__LECM$") == 0) {
            var childAuth = []
            getLECMGroups(authority, childAuth);
            result.push({
                name: authority.properties["cm:authorityName"],
                subGroups: childAuth
            });
        }
    }
}

var authorities = search.xpathSearch("/sys:system/sys:authorities");

var result = [];

getLECMGroups(authorities[0], result);

model.result = result;