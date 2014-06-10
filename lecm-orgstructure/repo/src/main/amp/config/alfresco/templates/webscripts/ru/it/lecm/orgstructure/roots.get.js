var root = {};
if (url.templateArgs.root_type !== null) {
    root = orgstructure.getRoot(url.templateArgs.root_type);
    var oRoot =  eval("(" + root + ")");
    model.root = oRoot;
}