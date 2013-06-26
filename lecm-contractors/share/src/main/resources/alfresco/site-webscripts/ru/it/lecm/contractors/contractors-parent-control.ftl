<#import "/ru/it/lecm/base-share/components/view2.lib.ftl" as view />

<#assign formId = args.htmlid + "-form" />

<#-- Будет уникальным на основе 'args.itemId' -->
<#assign viewParentFormId = args.itemId + "-view-parent-form" />
<#assign currentContractorRef = args.itemId />

<@view.viewForm formId = viewParentFormId useDefaultForms = true />

<div id="${formId}-view-parent-field" class="form-field" style="display: none;">
    <div class="viewmode-field">
        <span class="viewmode-label">Материнская компания:</span>
        <span class="viewmode-value"><a id="${formId}-view-parent-link" href="javascript:void(0);"/></a></span>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
    YAHOO.util.Event.onContentReady( "${formId}-view-parent-field", function() {

        // Спасаем "тонущие" всплывающие сообщения.
        Alfresco.util.PopupManager.zIndex = 9000;

        Alfresco.util.Ajax.request({

            method: "POST",
            requestContentType: "application/json",
            responseContentType: "application/json",

            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/getparent",

            dataObj: {
                "childContractor": "${currentContractorRef}"
            },

            successCallback:{
                fn: function( response ) {

                    var linkElem;

                    if( response.json.status === "success" ) {

                        linkElem = YAHOO.util.Dom.get( "${formId}-view-parent-link" );
                        linkElem.innerHTML = response.json.parentContractorName;

                        YAHOO.util.Event.addListener( linkElem, "click", function() {
                            LogicECM.CurrentModules.ViewFormModule[ "${viewParentFormId}" ].view( response.json.parentContractor, "contacts", "Просмотр материнской компании для " + response.json.childContractorName );
                        });

                        YAHOO.util.Dom.setStyle( "${formId}-view-parent-field", "display", "block" );
                    }
                }
            },

            execScripts: true
        });
    });
//]]>
</script>