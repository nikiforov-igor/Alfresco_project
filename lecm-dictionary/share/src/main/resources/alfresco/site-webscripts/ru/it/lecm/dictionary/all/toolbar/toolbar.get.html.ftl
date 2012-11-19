<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.AllDictToolbar("${id}").setOptions(
        {
            siteId: "site"
        }).setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="dictionary-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
            <div>
                ${msg('logicecm.dictionary.dictionary-list')}
            </div>
            <div class="import-xml">
                <span id="${id}-importXmlButton" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button" title="${msg('button.import-xml')}">&nbsp;</button>
                    </span>
                </span>

                <div id="${id}-import-xml-form-container" class="form-container" title="${msg('button.import-xml')}">
                    <form method="post" id="import-xml-form" enctype="multipart/form-data"
                          action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
                        <input type="file" id="import-xml-input" name="f" accept=".xml,application/xml,text/xml">
                    </form>
                </div>
            </div>
		</div>
	</div>
</div>