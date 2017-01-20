(function () {
    YAHOO.Bubbling.on('loadDefaultAuthorAction', loadValue);
    function loadValue(layer, args) {
        var formID = args[1].formId;
        var componentReadyElId = LogicECM.module.Base.Util.getComponentReadyElementId(formID, "lecm-document:author-assoc");
        YAHOO.util.Event.onContentReady(componentReadyElId, function () {
            var authorControl = Alfresco.util.ComponentManager.find({id: formID + "_assoc_lecm-document_author-assoc"})[0];
            authorControl.loadDefaultValue();
        });
    }
})();