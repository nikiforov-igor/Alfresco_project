function generateTempdirName() {
    var time = new Date().getTime();
    var random = (Math.random() * 999).toFixed(0);
    var formatRandom = "000".substring(0, 3 - random.length) + random;
    var name = time + formatRandom;
    return name;
}

var tempDirectory = lecmRepository.getTempRef(person);
if (tempDirectory == null) {
    tempDirectory = lecmRepository.createTemp(person);
}
var connectionsTemp = tempDirectory.childByNamePath("connections_temp");
if (connectionsTemp == null) {
    connectionsTemp = tempDirectory.createNode("connections_temp", "cm:folder", "cm:contains");
    tempDirectory.addAspect("sys:temporary");
}

var tempdir = connectionsTemp.createNode(generateTempdirName(), "cm:folder", "cm:contains");
model.nodeRef = tempdir.nodeRef.toString();