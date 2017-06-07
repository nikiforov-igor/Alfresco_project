function main() {
    var ownerRef = args["owner"];

    var uri = "/lecm/orgstructure/api/getEmployeePhoto?nodeRef=" + encodeURIComponent(ownerRef);
    var connector = remote.connect("alfresco");
    var response = connector.get(uri);
    model.json = response;
    if (response.status == 200) {
        var json = JSON.parse(response);

        var ref = json.nodeRef;
        if (ref) {
            var imgRef = url.context + "/proxy/alfresco/api/node/" + ref.replace(":/", "") + "/content";
            if (imgRef != "") {
                model.imageId = ref.slice(ref.lastIndexOf('/') + 1);
                model.imgRef = imgRef;
            }
        }
    }

    uri = "/lecm/substitude/format/node";
    var postBody = {
        nodeRef: ownerRef,
        substituteString: "{lecm-orgstr:employee-first-name} {lecm-orgstr:employee-middle-name} {lecm-orgstr:employee-last-name}"
    };
    response = connector.post(uri, jsonUtils.toJSONString(postBody), "application/json");
    if (response.status == 200) {
        var json = JSON.parse(response);
        model.fio = json.formatString;
    }

    uri = "/lecm/orgstructure/api/getEmployeePositions?nodeRef=" + encodeURIComponent(ownerRef);
    response = connector.get(uri);
    if (response.status == 200) {
        var positions = JSON.parse(response);
        if (positions) {
            uri = "/lecm/substitude/format/node";
            postBody = {
                substituteString: "{../lecm-orgstr:element-full-name}"
            };
            var positionsObjects = [];
            positions.forEach(function (position) {
                postBody.nodeRef = position.nodeRef;
                response = connector.post(uri, jsonUtils.toJSONString(postBody), "application/json");
                if (response.status == 200) {
                    var json = JSON.parse(response);
                    var department = json.formatString;
                    positionsObjects.push({
                        position: position.positionName,
                        department: department
                    });
                }
            });
            model.positionsObjects = positionsObjects;
        }
    }

    uri = "/lecm/orgstructure/api/getCurrentEmployee";
    response = connector.get(uri);
    if (response.status == 200) {
        var currentEmployee = JSON.parse(response);
        if (currentEmployee) {
            uri = "/lecm/substitude/format/node";
            postBody.nodeRef = ownerRef;
            postBody.substituteString = "{lecm-secretary-aspects:secretary-assoc-ref},{..lecm-d8n:delegation-opts-owner-assoc/lecm-d8n:delegation-opts-trustee-assoc(lecm-orgstr:employee-short-name=" + currentEmployee.shortName + ")/lecm-orgstr:employee-short-name},{..lecm-absence:abscent-employee-assoc(lecm-absence:activated=true)/lecm-absence:begin},{..lecm-absence:abscent-employee-assoc(lecm-absence:activated=true)/lecm-absence:end},{..lecm-absence:abscent-employee-assoc(lecm-absence:activated=true)/lecm-absence:unlimited}";
            response = connector.post(uri, jsonUtils.toJSONString(postBody), "application/json");
            if (response.status == 200) {
                var json = JSON.parse(response);
                var formatString = json.formatString;
                var delegations = formatString.split(",");
                var secretary = delegations[0];
                var delegation = delegations[1];
                var absenceBegin = delegations[2];
                var absenceEnd = delegations[3];
                var absenceUnlimited = delegations[4] == "true";
                if (secretary && secretary.indexOf(currentEmployee.nodeRef) != -1) {
                    model.secretaryText = this.msg.get("label.delegation.secretary-limitless");
                }
                if (delegation && absenceBegin) {
                    model.delegationText = this.msg.get("label.delegation.delegate") + " - " + this.msg.get("label.delegation.delegate.from") + " " + absenceBegin.substr(0, absenceBegin.length - 5) + " " + (absenceUnlimited ? "" : this.msg.get("label.delegation.delegate.to") + " " + absenceEnd.substr(0, absenceEnd.length - 5));
                }
            }
        }
    }
};
main();