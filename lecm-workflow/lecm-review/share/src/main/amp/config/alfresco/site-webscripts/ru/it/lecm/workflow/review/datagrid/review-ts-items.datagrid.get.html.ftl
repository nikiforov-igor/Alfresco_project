<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#assign bubblingLabel = args['itemId'] + '-review-ts-items'/>
<#assign datagridId = args.htmlid + '-' + bubblingLabel/>

<div id='${datagridId}'>
<@grid.datagrid id=datagridId showViewForm=true showArchiveCheckBox=false>
	<script type='text/javascript'>//<![CDATA[
		(function () {
			function _initReviewTsItemsDatagrid(layer, args) {
				if (this.options.bubblingLabel == args[1].datagrid.options.bubblingLabel) {
					YAHOO.Bubbling.unsubscribe(layer, _initReviewTsItemsDatagrid);
					YAHOO.Bubbling.fire('activeGridChanged', {
						bubblingLabel: this.options.bubblingLabel,
						datagridMeta: {
							itemType: 'lecm-review-ts:review-table-item',
							nodeRef: '${args['itemId']}'
						}
					});
				}
			}

			function initReviewTsItemsDatagrid() {
				var datagrid = new LogicECM.module.Base.DataGrid('${datagridId}');
				datagrid.setMessages(${messages});
				datagrid.setOptions({
					dataSource: 'lecm/review/ds/reviewItemsDS',
					attributeForShow: 'lecm-document:indexTableRow',
					refreshAfterCreate: true,
					usePagination: false,
					disableDynamicPagination: false,
					showExtendSearchBlock: false,
					overrideSortingWith: false,
					showCheckboxColumn: false,
					showActionColumn: false,
					expandable: false,
					bubblingLabel: '${bubblingLabel}'
				});

				YAHOO.Bubbling.on('initDatagrid', _initReviewTsItemsDatagrid, datagrid);
			}
			LogicECM.module.Base.Util.loadResources([
				'scripts/lecm-base/components/advsearch.js',
				'scripts/lecm-base/components/lecm-datagrid.js'
			], [
			], initReviewTsItemsDatagrid);
		})();
	//]]></script>
</@>
</div>
