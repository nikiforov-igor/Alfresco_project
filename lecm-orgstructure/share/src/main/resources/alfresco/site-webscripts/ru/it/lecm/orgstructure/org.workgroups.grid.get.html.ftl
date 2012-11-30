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
					//complete initial search
					var initialData = {
						datatype:this.datagridMeta.itemType
					};

					var searchConfig = this.datagridMeta.searchConfig;
					var sorting, filter, fullText;
					if (searchConfig) {
						filter = searchConfig.filter;
						sorting = searchConfig.sort != null && searchConfig.sort.length > 0 ? searchConfig.sort : "cm:name|true"; // по умолчанию поиск по свойству cm:name по убыванию
						fullText = searchConfig.fullTextSearch;
					}
					// trigger the initial search
					YAHOO.Bubbling.fire("doSearch",
							{
								searchSort:sorting,
								searchQuery:YAHOO.lang.JSON.stringify(initialData),
								searchFilter:filter,
								fullTextSearch:fullText,
								searchShowInactive:false,
								bubblingLabel:me.options.bubblingLabel
							});
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
						//complete initial search
						var initialData = {
							datatype:"lecm-orgstr:workforce"
						};
						if (me.datagridMeta.searchConfig == undefined) {
							me.datagridMeta.searchConfig = {};
						}
						me.datagridMeta.searchConfig.sort = "";
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
											fullDelete:true
										},
										searchConfig:{
											filter:'PARENT:\"' + selectItem.getData().nodeRef + '\"',
											sort: "cm:name|true"
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
							bubblingLabel:"workGroup",
							usePagination:true,
							showExtendSearchBlock:false
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
