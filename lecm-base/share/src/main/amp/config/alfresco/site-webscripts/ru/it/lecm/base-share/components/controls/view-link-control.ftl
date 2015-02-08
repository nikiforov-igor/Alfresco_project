<#import "/ru/it/lecm/base-share/components/view2.lib.ftl" as view />

<#assign formId = args.htmlid + "-form" />

<#-- Будет уникальным на основе 'args.itemId' -->
<#assign viewLinkFormId = args.itemId + "-view-link-form" />
<#assign currentItemRef = args.itemId />

<#assign dataUrl = field.control.params.dataUrl />
<#assign labelText = msg(field.control.params.labelText) />
<#assign formTitle = msg(field.control.params.formTitle) />

<#if field.control.params.setId??>
    <#assign setId = field.control.params.setId />
<#else>
    <#assign setId = "common" />
</#if>

<@view.viewForm formId = viewLinkFormId useDefaultForms = true />

<div class="control view-link viewmode">
	<div class="label-div">
		<label>${labelText}:</label>
	</div>
	<div id="${formId}-view-link-field" class="container">
		<div class="value-div">
			<a id="${formId}-view-link-ref" href="javascript:void(0);"/></a>
		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
    YAHOO.util.Event.onContentReady( "${formId}-view-link-field", function() {

        // Спасаем "тонущие" всплывающие сообщения.
        Alfresco.util.PopupManager.zIndex = 9000;

        Alfresco.util.Ajax.request({

            method: "POST",
            requestContentType: "application/json",
            responseContentType: "application/json",

            url: Alfresco.constants.PROXY_URI_RELATIVE + "${dataUrl}",

            dataObj: {
                "childRef": "${currentItemRef}"
            },

            successCallback:{
                fn: function( response ) {

                    var linkElem;

                    if( response.json.status === "success" ) {

                        linkElem = YAHOO.util.Dom.get( "${formId}-view-link-ref" );
                        linkElem.innerHTML = response.json.parentName;

                        YAHOO.util.Event.addListener( linkElem, "click", function() {
                            LogicECM.CurrentModules.ViewFormModule[ "${viewLinkFormId}" ].view( response.json.parentRef, "${setId}", "${formTitle} " + response.json.childName );
                        });
                    }
                }
            },

            execScripts: true
        });
    });
//]]>
</script>