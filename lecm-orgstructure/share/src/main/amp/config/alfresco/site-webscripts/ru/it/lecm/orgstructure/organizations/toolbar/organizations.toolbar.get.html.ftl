<#import '/ru/it/lecm/base-share/components/base-components.ftl' as toolbar/>

<#assign bubblingLabel = 'organizations-list'/>
<#assign toolbarId = args.htmlid + '-organizations-list-toolbar'/>

<div id='${toolbarId}'>
<@toolbar.baseToolbar id=toolbarId showButtons=true showSearchBlock=true showExSeacrhBtn=true>
	<div class='create-row'>
		<span id='${toolbarId}-newRowButton' class='yui-button yui-push-button'>
			<span class='first-child'>
				<button type='button'>${msg('logicecm.orgstructure.add-element')}</button>
			</span>
		</span>
	</div>
	<script type='text/javascript'>//<![CDATA[
		(function () {
			function initOrganizationsToolbar() {
				new LogicECM.module.OrgStructure.OrganizationsList.Toolbar('${toolbarId}', {
					bubblingLabel: '${bubblingLabel}'
				}, ${messages});
			}

			LogicECM.module.Base.Util.loadResources([
				'scripts/lecm-base/components/lecm-toolbar.js',
				'scripts/lecm-orgstructure/organizations-list-toolbar.js'
			], [
				'components/data-lists/toolbar.css'
			], initOrganizationsToolbar);
		})();
	//]]></script>
</@>
</div>
