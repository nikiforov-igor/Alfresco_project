<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.DataListToolbar("${id}").setOptions(
		{
			siteId: "site"
		}).setMessages(${messages});
//]]></script>

<style type="text/css">
    .datalist-toolbar .export span.first-child
    {
        background: url(/share/res/components/images/task-16.png) no-repeat 12px 4px;
        padding-left: 24px;
    }
    .datalist-toolbar .delete-row span.first-child
    {
        background: url(/share/res/components/images/delete-row-16.png) no-repeat 12px 4px;
        padding-left: 24px;
    }
</style>

<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
			<div class="create-row">
                <span id="${id}-newRowButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button">${msg('logicecm.dictionary.add-element')}</button>
                   </span>
                </span>
			</div>
			<div class="delete-row">
                <span id="${id}-deleteButton" class="yui-button yui-push-button">
                   <span class="first-child" class="onActionDelete">
                      <button type="button" title="${msg('menu.selected-items.delete')}"/>
                   </span>
                </span>
			</div>
            <div class="export">
                <span id="${id}-exportButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="${msg('button.export')}"/>
                   </span>
                </span>
			</div>
			<div class="new-row">
            <span id="${id}-importButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.import')}</button>
               </span>
            </span>
			</div>
		</div>

		<div class="right" style="display: none;">

		</div>
	</div>
</div>