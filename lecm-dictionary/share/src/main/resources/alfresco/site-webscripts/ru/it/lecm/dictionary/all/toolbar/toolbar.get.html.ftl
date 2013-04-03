<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
function init() {
	new window.LogicECM.module.AllDictionary.Toolbar("${id}").setMessages(${messages});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>
<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button">
		<div class="left">
            <div>
                ${msg('logicecm.dictionary.dictionary-list')}
            </div>
            <div class="divider"></div>
            <div>
                <form method="post" id="${id}-import-xml-form" enctype="multipart/form-data"
                      action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
                    <div id="${id}-import-xml" class="import-xml" title="${msg('button.import-xml')}">
                        <span id="${id}-importXmlButton" class="yui-button yui-push-button">
                            <span class="first-child">
                                <button type="button" title="${msg('button.import-xml')}">&nbsp;</button>
                            </span>
                        </span>
                        <input type="file" id="${id}-import-xml-input" name="f" accept=".xml,application/xml,text/xml">
                    </div>
                </form>
            </div>
		</div>
	</div>
</div>