var DEFAULT_DELIMITER = ",";
var SUB_ELEMENT_DELIMITER = ".";

function generateNodeName(form, pattern, delimiter, override) {
    // for hidden - prop_cm_name
    var nameElement = form['prop_cm_name'];
    if (nameElement != null && pattern != null) {
        var elValue = nameElement.value;
        // if cm:name field empty or override it
        if (elValue == null || elValue == "" || override) {
            var props = pattern.split(delimiter != null ? delimiter : DEFAULT_DELIMITER);
            nameElement.value = createName(form, props);
        }
    }
}

function createName(form, nameParts) {
    var result = "";
    for (i = 0, ii = nameParts.length; i < ii; i++) {
        var part = nameParts[i];

        var prop, usedLen;
        var startBr = part.indexOf("{");
        if (startBr > 0) { // used word, not property
            var lastBr = part.indexOf("}"); // must ended on '}'
            if (lastBr > 0) {
                prop = part.substring(startBr + 1, lastBr);
            } else {
                break;
            }
            // simple add word to result
            result = result + prop + SUB_ELEMENT_DELIMITER;
        } else { // if property used
            startBr = part.indexOf("[");
            if (startBr > 0) {  // check used length
                usedLen = part.slice(startBr + 1, part.indexOf("]"));
                prop = part.substring(0, startBr);
            } else {
                prop = part; // used all property string length
                usedLen = null
            }
            // try get property from form
            var propElement = form["prop_" + prop];
            if (propElement != null) {
                var propValue = propElement.value;
                if (usedLen != null && usedLen.length > 0) {
                    propValue = propValue.substring(0, usedLen);
                }
                result = result + propValue + SUB_ELEMENT_DELIMITER;
            }
        }
    }
    result = (result.length > 0) ? result.substring(0, result.length - SUB_ELEMENT_DELIMITER.length) : result; //delete last element delimiter
    return result;
}
