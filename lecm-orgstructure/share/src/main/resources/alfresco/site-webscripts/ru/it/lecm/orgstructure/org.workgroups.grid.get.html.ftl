<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div id="orgstructure-workgroup-grid">
	<div id="yui-main-3">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkgroupDatagrid() {

				LogicECM.module.Base.DataGrid.prototype._setupDataTable = function (columnDefinitions, me) {

					var dTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, this.widgets.dataSource,
							{
								renderLoopSize:this.options.usePagination ? 16 : 32,
								initialLoad:false,
								dynamicData:false,
								"MSG_EMPTY":this.msg("message.empty"),
								"MSG_ERROR":this.msg("message.error"),
								paginator:this.widgets.paginator
							});

					// Update totalRecords with value from server
					dTable.handleDataReturnPayload = function DataGrid_handleDataReturnPayload(oRequest, oResponse, oPayload) {
						me.totalRecords = oResponse.meta.totalRecords;
						oResponse.meta.pagination =
						{
							rowsPerPage:me.options.pageSize,
							recordOffset:(me.currentPage - 1) * me.options.pageSize
						};
						return oResponse.meta;
					};

					// Override abstract function within DataTable to set custom error message
					dTable.doBeforeLoadData = function DataGrid_doBeforeLoadData(sRequest, oResponse, oPayload) {
						if (oResponse.error) {
							try {
								var response = YAHOO.lang.JSON.parse(oResponse.responseText);
								me.widgets.dataTable.set("MSG_ERROR", response.message);
							}
							catch (e) {
								me._setDefaultDataTableErrors(me.widgets.dataTable);
							}
						}

						// We don't get an renderEvent for an empty recordSet, but we'd like one anyway
						if (oResponse.results.length === 0) {
							this.fireEvent("renderEvent",
									{
										type:"renderEvent"
									});
						}

						// Must return true to have the "Loading..." message replaced by the error message
						return true;
					};

					// Override default function so the "Loading..." message is suppressed
					dTable.doBeforeSortColumn = function DataGrid_doBeforeSortColumn(oColumn, sSortDir) {
						me.currentSort =
						{
							oColumn:oColumn,
							sSortDir:sSortDir

						};
						me.sort = {
							enable: true
						}
						return true;
					};

					// Событие когда выбранны все элементы
					YAHOO.util.Event.onAvailable(this.id + "-select-all-records", function () {
						YAHOO.util.Event.on(this.id + "-select-all-records", 'click', this.selectAllClick, this, true);
					}, this, true);

					// File checked handler
					dTable.subscribe("checkboxClickEvent", function (e) {
						var id = e.target.value;
						this.selectedItems[id] = e.target.checked;

						var checks = Selector.query('input[type="checkbox"]', dTable.getTbodyEl()),
								len = checks.length, i;

						var allChecked = true;
						for (i = 0; i < len; i++) {
							if (!checks[i].checked) {
								allChecked = false;
								break;
							}
						}
						Dom.get(this.id + '-select-all-records').checked = allChecked;
//						Bubbling.fire("selectedItemsChanged");
					}, this, true);

					// Сортировка. Событие при нажатии на название столбца.
					dTable.subscribe("beforeRenderEvent",function () {
						var dataGrid = me.modules.dataGrid;
						var datagridMeta = dataGrid.datagridMeta;

						if (me.currentSort){
							if (me.elTh == null) {
								me.elTh = me.currentSort.oColumn.getThEl();
							}
							if (me.elTh == me.currentSort.oColumn.getThEl()) {
								if (me.currentSort.sSortDir == YAHOO.widget.DataTable.CLASS_DESC){
									Dom.addClass(me.currentSort.oColumn.getThEl(), YAHOO.widget.DataTable.CLASS_DESC);
								} else {
									Dom.removeClass(me.currentSort.oColumn.getThEl(), YAHOO.widget.DataTable.CLASS_DESC);
									Dom.addClass(me.currentSort.oColumn.getThEl(), YAHOO.widget.DataTable.CLASS_ASC);
								}

							} else {
								Dom.removeClass(me.elTh, YAHOO.widget.DataTable.CLASS_DESC);
								Dom.removeClass(me.elTh, YAHOO.widget.DataTable.CLASS_ASC)
								me.elTh = me.currentSort.oColumn.getThEl();
							}
						}
						if (me.sort) {
							if (datagridMeta.searchConfig == undefined) {
								datagridMeta.searchConfig = {};
							}
							datagridMeta.searchConfig.sort = "";
							// Если ассоциация, то не сортируем
							if (me.currentSort.oColumn.field.indexOf("assoc_") != 0) {
								if (me.desc) {
									datagridMeta.searchConfig.sort = me.currentSort.oColumn.field.replace("prop_","").replace("_",":") +
											"|false";
									me.desc = false;
									me.currentSort.sSortDir = YAHOO.widget.DataTable.CLASS_DESC;

								} else {
									datagridMeta.searchConfig.sort = me.currentSort.oColumn.field.replace("prop_", "").replace("_", ":") +
											"|true";
									me.desc = true;
									me.currentSort.sSortDir = YAHOO.widget.DataTable.CLASS_ASC;
								}
								//complete initial search
								var initialData = {
									datatype:datagridMeta.itemType
								};
								var searchConfig = datagridMeta.searchConfig;
								var sorting, filter, fullText;
								filter = searchConfig.filter;
								fullText = searchConfig.fullTextSearch;
								if (me.sort){
									// Обнуляем сортировку иначе зациклится.
									me.sort = null;
									YAHOO.Bubbling.fire("doSearch",
											{
												searchSort:datagridMeta.searchConfig.sort,
												searchQuery:YAHOO.lang.JSON.stringify(initialData),
												searchFilter:filter,
												fullTextSearch:fullText,
												bubblingLabel:me.options.bubblingLabel
											});
								}
							}
						}
						if (me._hasEventInterest("workGroup")) {
						YAHOO.Bubbling.fire("initActiveButton",{
							bubblingLabel: "workForce",
							disable: true});
						}
					},
					dTable, true);

					// Rendering complete event handler
					dTable.subscribe("renderEvent", function () {
						Alfresco.logger.debug("DataTable renderEvent");

						// Deferred functions specified?
						for (var i = 0, j = this.afterDataGridUpdate.length; i < j; i++) {
							this.afterDataGridUpdate[i].call(this);
						}
						this.afterDataGridUpdate = [];
					}, this, true);

					// Enable row highlighting
					dTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
					dTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);

					if (this._hasEventInterest("workGroup")) {
						dTable.subscribe("rowClickEvent", this.onEventSelectRow, this, true);
					}

					if (this.options.height != null) {
						YAHOO.util.Dom.setStyle(this.id + "-grid", "height", this.options.height + "px");
					}

					return dTable;

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
						var me = this;
						// Отрисовка датагрида если указан ItemType
						YAHOO.Bubbling.fire("activeGridChanged",
								{
									datagridMeta:{
										itemType: "lecm-orgstr:workforce", // тип объектов,
										// которые будут рисоваться в гриде (обязателен)
										nodeRef: selectItem.getData().nodeRef, // ссылка на текущую(корневую) ноды (необязателен)
										title: '', // для вывода заголовка в гриде (необязателен)
										description: '', // для вывода описания в заголовке грида (необязателен)
										actionsConfig: {// настройки экшенов. (необязателен)
											fullDelete:true // если true - удаляем ноды, иначе выставляем им флаг
											// "неактивен"
										},
										searchConfig:{ //настройки поиска (необязателен)
											filter:'PARENT:\"' + selectItem.getData().nodeRef + '\"'
													+ ' AND (NOT (ASPECT:"lecm-dic:aspect_active") OR lecm\\-dic:active:true)', // дополнительный запрос(фильтр)
											sort: "cm:name|true" // сортировка. Указываем по какому полю и порядок (true - asc), например, cm:name|true
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
