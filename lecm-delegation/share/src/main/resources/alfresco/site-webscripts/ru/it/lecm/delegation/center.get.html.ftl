<#assign centerId = args.htmlid>
<!--[if IE]>
	<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript"> //<![CDATA[
	(function () {
		// var delegationCenter = new LogicECM.module.Delegation.Center ("${centerId}");
		// delegationCenter.setMessages(${messages});
		var datagrid = new LogicECM.module.Delegation.DataGrid ("${centerId}");
		datagrid.setOptions ({
			usePagination: ${(args.pagination!false)?string},
			initialFilter: null
		});
		datagrid.setMessages (${messages});
	})();
//]]>
</script>

<div id="${centerId}-body" class="datagrid">
	<div id="${centerId}-datagridBar" class="yui-ge datagrid-bar flat-button">
		<div class="yui-u first align-center">
			<div class="item-select">
				<button id="${centerId}-itemSelect-button" name="datagrid-itemSelect-button">Отметить</button>
				<div id="${centerId}-itemSelect-menu" class="yuimenu">
					<div class="bd">
						<ul>
							<li><a href="#"><span class="selectAll">Отметить всех</span></a></li>
							<li><a href="#"><span class="selectInvert">Инвертировать отметки</span></a></li>
							<li><a href="#"><span class="selectNone">Снять все отметки</span></a></li>
						</ul>
					</div>
				</div>
			</div>
			<div id="${centerId}-paginator" class="paginator"></div>
		</div>
		<div class="yui-u align-right">
			<div class="items-per-page">
				<button id="${centerId}-itemsPerPage-button">Записей на странице</button>
			</div>
		</div>
	</div>
	<div id="${centerId}-grid" class="grid"></div>
	<div id="${centerId}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">
		<div class="yui-u first align-center">
			<div class="item-select">&nbsp;</div>
			<div id="${centerId}-paginatorBottom" class="paginator"></div>
		</div>
	</div>
</div>
