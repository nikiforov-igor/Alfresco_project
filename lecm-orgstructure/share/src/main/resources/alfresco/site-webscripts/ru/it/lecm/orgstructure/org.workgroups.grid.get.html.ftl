<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div id="orgstructure-workgroup-grid">
	<div id="yui-main-3">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkgroupDatagrid() {
				LogicECM.module.Base.DataGrid.prototype.setupDataTable = function (columns) {
					// YUI DataTable colum
					var columnDefinitions = this.getDataTableColumnDefinitions();
					// DataTable definition
					var me = this;
					if (!this.widgets.dataTable) {
						this.widgets.dataTable = this._setupDataTable(columnDefinitions, me);
						this.widgets.dataTable.subscribe("beforeRenderEvent", function () {
									me.beforeRenderFunction();
									if (me._hasEventInterest("workGroup")) {
										YAHOO.Bubbling.fire("initActiveButton", {
											bubblingLabel:"workForce",
											disable:true});
									}
								},
								me.widgets.dataTable, true);
						if (this._hasEventInterest("workGroup")) {
							this.widgets.dataTable.subscribe("rowClickEvent", this.onEventSelectRow, this, true);
						}
						// link current table with search and do search
						this.modules.search.dataTable = this.widgets.dataTable;
					}
					var searchConfig = this.datagridMeta.searchConfig;
					if (searchConfig) { // Поиск через SOLR
						if (searchConfig.sort == null || searchConfig.sort.length == 0) {
							searchConfig.sort = "cm:name|true"; // по умолчанию поиск по свойству cm:name по убыванию
						}
						if (searchConfig.parent == null || searchConfig.parent.length == 0){
							searchConfig.parent = this.datagridMeta.nodeRef;
						}
						searchConfig.formData = {
							datatype:this.datagridMeta.itemType
						};
						YAHOO.Bubbling.fire("doSearch",
								{
									searchConfig:searchConfig,
									searchShowInactive:false,
									bubblingLabel:me.options.bubblingLabel
								});
					} else { // Поиск без использования SOLR
						YAHOO.Bubbling.fire("doSearch",
								{
									parent:this.datagridMeta.nodeRef,
									itemType:this.datagridMeta.itemType,
									searchShowInactive:false,
									bubblingLabel:me.options.bubblingLabel
								});
					}
				};
				/**
				 * Выделение строки в таблице
				 */
				LogicECM.module.Base.DataGrid.prototype.onEventSelectRow = function DataGrid_onEventSelectRow(oArgs){
					// Проверка а из той ли песочницы (два dataGrida) мы вызвали метод. Переопределяz метод мы
					// переопределяем его для всех песочниц на странице.
					if (this._hasEventInterest("workGroup")) {
						// Выделяем строку в DataGrid
						this.widgets.dataTable.onEventSelectRow(oArgs);
						// Перерисовываем таблицу, здесь
						var me = this;
						// Номер строки в таблице
						var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
						// Выбранный элемент
						var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
						// Отрисовка датагрида для Участников Рабочих групп
						YAHOO.Bubbling.fire("activeGridChanged",
								{
									datagridMeta:{
										itemType: "lecm-orgstr:workforce",
										nodeRef: selectItem.getData().nodeRef,
										actionsConfig: {
											fullDelete:true,
											targetDelete:true
										}
									},
									bubblingLabel: "workForce"
								});
						// Активируем кнопку "Новый элемент" в правом TollBar-е
						YAHOO.Bubbling.fire("initActiveButton",{
							bubblingLabel: "workForce",
							disable: false});
					}
				};

				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							bubblingLabel:"${bubblingLabel!"workGroup"}",
							usePagination:true,
							showExtendSearchBlock:false,
							actions: [
								{
									type:"action-link-workGroup",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"action-link-workGroup",
									id:"onActionVersion",
									permission:"edit",
									label:"${msg("actions.version")}"
								},
								{
									type:"action-link-workGroup",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}"
								}
							],
							showCheckboxColumn: false
						}).setMessages(${messages});
			}

			function init() {
                createWorkgroupDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
