var nodeRef = 'empty';
for (arg in args) {
    if (arg == 'nodeRef') {
        nodeRef = args[arg];
    }
}

var experts = [];

if (nodeRef != 'empty') {
    var cdoc = search.findNode(nodeRef);
    if (cdoc != null) {
        var expertsList = cdoc.assocs["lecm-exp:experts"];
        addExperts(experts, expertsList);
    }
}

model.experts = experts;

function addExperts(experts, expertsList) {
    for (var index in expertsList) {
        var expertNode = expertsList[index];

        login = expertNode.properties["cm:userName"];
        first = expertNode.properties["cm:firstName"];
        last = expertNode.properties["cm:lastName"];
        ref = expertNode.getNodeRef().toString();

        experts.push({
            lname:login,
            fname:first + " " + last,
            nodeRef:ref
        });
    }
}