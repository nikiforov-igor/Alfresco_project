<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign datagridId = id + "-favoriteDocsDatagrid">

<div class="dashlet contracts-favorite bordered">
	<div class="title dashlet-title">
		<span>${msg("label.title")}</span>
	</div>
	<div class="body scrollableList dashlet-body" id="${id}_results">
		<@grid.datagrid datagridId false "" false/>
	</div>
</div>

<script type="text/javascript">
(function() {
	LogicECM.module.Base.Util.loadResources(
		['scripts/lecm-base/components/lecm-datagrid.js', 'scripts/grids/documents-journal-grid.js'],
		['css/lecm-contracts/contracts-favorite.css'],
		function() {
			var datagrid = new LogicECM.module.DocumentsJournal.DataGrid('${datagridId}');
			datagrid.setMessages('${messages}');
			datagrid.setOptions({
				usePagination: false,
				// pageSize: 5,
				showExtendSearchBlock: false,
				actions: [],
				allowCreate: false,
				showActionColumn: false,
				showCheckboxColumn: false,
				bubblingLabel: 'favorite-docs-list',
				attributeForShow: 'lecm-document:present-string',
				datagridMeta: {
					// itemType: 'lecm-contract:document,lecm-additional-document:additionalDocument',
					itemType: 'lecm-contract:document',
					datagridFormId: 'datagrid_Избранное',
					nodeRef: null,
					sort: 'cm:modified|false',
					searchConfig: {
						filter: '({{FAVOURITES}})',
						formData: {
							datatype: 'lecm-contract:document,lecm-additional-document:additionalDocument'
						}
					}
				}
			});
			datagrid.draw();
		}
	);
})();
</script>
