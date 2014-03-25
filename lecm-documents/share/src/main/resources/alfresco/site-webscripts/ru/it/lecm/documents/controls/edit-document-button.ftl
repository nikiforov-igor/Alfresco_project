<#assign documentRef = "${form.arguments.itemId}"?string/>
<#assign id = documentRef?replace("workspace://SpacesStore/", "")?replace("-", "")/>
<div class="edit-button">
    <div class="metadata-form">
        <div class="lecm-dashlet-actions">
            <a id="documentMetadata-${id}-action-edit" onclick="LogicECM.module.Document.editDocument('${id}', '${documentRef}')" class="edit"
               title="Редактировать сведения"></a>
        </div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
    // Ensure LogicECM root object exists
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};
    LogicECM.module.Document = LogicECM.module.Document || {};

    LogicECM.module.Document.docOpening = false;

    LogicECM.module.Document.editDocument = function onEdit(id, nodeRef, formId) {
        // Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
        if (LogicECM.module.Document.docOpening) {
            return;
        }
        LogicECM.module.Document.docOpening = true;

        var currentFormId = "edit-document";

        if (formId != undefined || formId != null) {
            currentFormId = formId;
        }

        var templateUrl = LogicECM.module.Document.generateCreateNewUrl(nodeRef, currentFormId);

        new Alfresco.module.SimpleDialog("documentMetadata-" + id + "_results").setOptions({
            width: "84em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow: {
                fn: function (p_form, p_dialog) {
                    var fileSpan = '<span class="light">Изменение атрибутов документа</span>';
                    Alfresco.util.populateHTML(
                            [p_dialog.id + "-form-container_h", fileSpan]
                    );

                    YAHOO.util.Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");

                    LogicECM.module.Document.docOpening = false;
                }
            },
            onSuccess: {
                fn: function (response) {
                    window.location.reload();
                },
                scope: this
            }
        }).show();
    };

    LogicECM.module.Document.generateCreateNewUrl = function (nodeRef, formId) {
        var templateUrl = Alfresco.constants.URL_SERVICECONTEXT +
                "lecm/components/form"
                + "?itemKind={itemKind}"
                + "&itemId={itemId}"
                + "&mode={mode}"
                + "&submitType={submitType}"
                + "&showCancelButton=true";
        return YAHOO.lang.substitute(templateUrl, {
            itemKind: "node",
            itemId: nodeRef,
            formId: formId,
            mode: "edit",
            submitType: "json"
        });
    };
</script>