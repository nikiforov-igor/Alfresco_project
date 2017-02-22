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

<div class="control view-link viewmode">
	<div class="label-div">
		<label>${labelText}:</label>
	</div>
	<div id="${formId}-view-link-field" class="container">
		<div id="${formId}-view-link-block" class="value-div">

		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
    YAHOO.util.Event.onContentReady( "${formId}-view-link-field", function() {

        // Спасаем "тонущие" всплывающие сообщения.
        Alfresco.util.PopupManager.zIndex = 9000;

        Alfresco.util.Ajax.jsonPost({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "${dataUrl}",
            dataObj: {
                "childRef": "${currentItemRef}"
            },
            successCallback:{
                fn: function(response) {
                    var blockElem;
                    if( response.json.status === "success" ) {
                        if (response.json.parentName) {
	                        blockElem = YAHOO.util.Dom.get( "${formId}-view-link-block" );
	                        blockElem.innerHTML = '<a id="${formId}-view-link-ref" href="javascript:void(0);"/>' + response.json.parentName + '</a>';
                            YAHOO.util.Event.addListener("${formId}-view-link-ref", "click", function () {
                                LogicECM.module.Base.Util.viewAttributes({
                                    formId: '${viewLinkFormId}',
                                    itemId: response.json.parentRef,
                                    setId: '${setId}',
                                    failureMessage: 'message.object-not-found',
                                    title: '${formTitle} ' + response.json.childName
                                });
                            });
                        } else if (response.json.parents != null) {
	                        blockElem = YAHOO.util.Dom.get( "${formId}-view-link-block" );

                            for (var i = 0; i < response.json.parents.length; i++) {
	                            blockElem.innerHTML += '<a id="${formId}-view-link-ref-' + i + '" href="javascript:void(0);"/>' + response.json.parents[i].name + '</a></br>';

	                            YAHOO.util.Event.onAvailable("${formId}-view-link-ref-" + i, function (j) {
                                    YAHOO.util.Event.addListener("${formId}-view-link-ref-" + j, "click", function (e, k) {
                                        LogicECM.module.Base.Util.viewAttributes({
                                            formId: '${viewLinkFormId}',
                                            itemId: response.json.parents[k].nodeRef,
                                            setId: '${setId}',
                                            failureMessage: 'message.object-not-found',
                                            title: '${formTitle} ' + response.json.childName
                                        });
                                    }, j);
	                            }, i);
                            }
                        }
                    }
                }
            },

            execScripts: true
        });
    });
//]]>
</script>