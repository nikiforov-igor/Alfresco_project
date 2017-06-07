(function () {
    var files = json.getJSONArray('files');

    for (var i = 0; i < files.length(); i++) {
        var file = files.get(i);
        process(file);
    }

    function process(file) {
        var nodeRef = file.get("nodeRef");
        var node = search.findNode(nodeRef);

        node.addAspect('lecm-document-aspects:complex-attachment');
        var folder = node.parent.createFolder(node.properties['cm:name'] + '_complex_attachment_pages');
        node.createAssociation(folder, 'lecm-document-aspects:complex-attachment-folder');
        node.properties.content.mimetype = 'complex-attachment';
        node.save();
    }
})();
