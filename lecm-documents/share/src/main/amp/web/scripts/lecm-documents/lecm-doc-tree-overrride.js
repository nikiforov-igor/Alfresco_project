YAHOO.util.Event.onContentReady("alf-hd", function () {
    var $combine = Alfresco.util.combinePaths;

    if (Alfresco.RepositoryDocListTree) {
        Alfresco.RepositoryDocListTree.prototype._buildTreeNodeUrl = function DLT__buildTreeNodeUrl(path) {
            var url = $combine("lecm/doclib/treenode/node/alfresco/company/home", Alfresco.util.encodeURIPath(path));
            url += "?perms=false";
            url += "&children=" + this.options.evaluateChildFolders;
            url += "&max=" + this.options.maximumFolderCount;
            url += "&libraryRoot=" + this.options.rootNode;
            return Alfresco.constants.PROXY_URI + url;
        }
    }
});
