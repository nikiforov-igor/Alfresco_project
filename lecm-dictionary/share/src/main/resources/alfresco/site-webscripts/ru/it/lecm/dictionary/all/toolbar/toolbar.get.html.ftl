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
                <button id="show-import-xml" title="${msg('button.import-xml')}">${msg('button.import-xml')}</button>
                <div id="import-xml-panel">
                    <div class="hd">
                        ${msg('title.select-file')}
                    </div>
                    <div class="bd">
                        <form method="post" id="import-xml-form" enctype="multipart/form-data"
                              action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
                            <p>
                                <input type="file" name="f">
                                <input type="button" id="import-xml-submit" value="Submit">
                            </p>
                        </form>
                    </div>
                </div>
            </div>
		</div>
	</div>
</div>