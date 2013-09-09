function _generatePropertyFilterStr(filter, property) {
    if ((filter && filter.length > 0) && (property && property.length > 0)) {
        var re = /\s*,\s*/;
        var values = filter.split(re);
        var shieldProp = property.split("-").join("\\-");
        var resultFilter = "";
        var notFilter = "";
        for (var i = 0; i < values.length; i++) {
            var value = values[i];
            if (value.indexOf("!") != 0) {
                resultFilter += "@" + shieldProp + ":\'" + value + "\' ";
            } else {
                value = value.replace("!","");
                notFilter += "@" + shieldProp + ":\'" + value + "\' ";
            }

        }
        return (resultFilter.length > 0 ? "(" + resultFilter + ")" : "")
            + (resultFilter.length > 0 && notFilter.length > 0 ? " AND " : "")
            + (notFilter.length > 0 ? "NOT (" + notFilter + ")" : "");
    }
    return "";
}

function _generatePathsFilterStr(pathsStr) {
    if (pathsStr) {
        var paths = pathsStr.split(",");
        var result = "";
        for (var i = 0; i < paths.length; i++) {
            if (result.length > 0) {
                result += " OR ";
            }
            result += 'PATH:"' + paths[i] + '//*"' ;
        }
        return result;
    }
    return "";
}