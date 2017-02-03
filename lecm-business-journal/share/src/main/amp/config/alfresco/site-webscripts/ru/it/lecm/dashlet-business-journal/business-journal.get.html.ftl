<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/dashlets/dashlet-business-journal.css" />
<@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>


<#assign id = args.htmlid>

<#if hasAccess>
	<script type="text/javascript">
	    //<![CDATA[
	    (function() {
	        var OBJECT_TYPE = "Тип объекта";
	        var EVENT_CATEGORY = "Категория события";
	        var SELECT_DAYS = {
	            defaultIndex: 0,
	            options: [
	                {
	                    value: '',
	                    text: '${msg("label.select.days.all")}'
	                },
	                {
	                    value: '1',
	                    text: '${msg("label.select.days.today")}'
	                },
	                {
	                    value: '2',
	                    text: '${msg("label.select.days.2days")}'
	                },
	                {
	                    value: '5',
	                    text: '${msg("label.select.days.5days")}'
	                },
	                {
	                    value: '10',
	                    text: '${msg("label.select.days.10days")}'
	                }
	            ]
	        };
	        var SELECT_WHOSE = {
	            defaultIndex: 0,
	            options: [
	                {
	                    value: '',
	                    text: '${msg("label.select.all")}'
	                },
	                {
	                    value: 'my',
	                    text: '${msg("label.select.whose.my")}'
	                }
	                <#--{-->
	                    <#--value: 'control',-->
	                    <#--text: '${msg("label.select.whose.control")}'-->
	                <#--}-->
	            ]
	        };
	        var DEPARTMENT_OPTION = {
	            value: 'department',
	            text: '${msg("label.select.whose.department")}'
	        };
	        var Dom = YAHOO.util.Dom,
	            Event = YAHOO.util.Event,
	            Selector = YAHOO.util.Selector;
	        var container,
                dashletResizer;

		    var loadItemsCount = 50;
		    var skipItemsCount = 0;

            function setMenuHeight(menuId) {
                if (dashletResizer) {
                    var height = parseInt(Dom.getStyle(dashletResizer.dashletBody, "height"));
                    var menu;

                    if (menuId) {
                        menu = Selector.query("#" + menuId + " div.bd");
                    } else {
                        menu = Selector.query("#${id}_controls div.yuimenu div.bd");
                    }
                    Dom.setStyle(menu, "max-height", height + "px");
                }
            }
	        function init() {
	            dashletResizer = new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
                dashletResizer.onResizeDefault = dashletResizer.onResize;
                dashletResizer.onResize = function() {
                    dashletResizer.onResizeDefault();
                    setMenuHeight();
                };

	            new Alfresco.widget.DashletTitleBarActions("${id?html}").setOptions({
	                actions: [
	                    {
	                        cssClass: "help",
	                        bubbleOnClick: {
	                            message: "${msg("dashlet.help")?js_string}"
	                        },
	                        tooltip: "${msg("dashlet.help.tooltip")?js_string}"
	                    }
	                ]
	            });

	            container = Dom.get('${id}_controls');

	            //получить nodeRef справочника "Тип объекта"
	            Alfresco.util.Ajax.jsonGet({
	                url: Alfresco.constants.PROXY_URI + "lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(OBJECT_TYPE),
	                successCallback: {
	                    fn: function(response){
	                        //заполнить выпадающий список типов объектов
	                        Alfresco.util.Ajax.jsonGet({
	                            url: Alfresco.constants.PROXY_URI + "lecm/dictionary/api/getChildrenItems.json?nodeRef=" + response.json.nodeRef,
	                            successCallback: {
	                                fn: function(response){
	                                    var items = response.json;

	                                    if (items) {
	                                        var SELECT_TYPES = {
	                                            defaultIndex: 0,
	                                            options: []
	                                        };

	                                        SELECT_TYPES.options[0] = {
	                                            value: '',
	                                            text: '${msg("label.select.types.all")}'
	                                        };
	                                        for (var i = 0; i < items.length; i++) { // [].forEach() не работает в IE
	                                            var item = items[i];

	                                            SELECT_TYPES.options[i + 1] = {
	                                                value: item.nodeRef,
	                                                text: item.name
	                                            };
	                                        }
	                                        makeSelect('${id}-types', SELECT_TYPES);
	                                    }
	                                },
	                                scope: this
	                            },
	                            failureCallback: {
	                                fn: function() {console.log("Failed to load Object types list.")},
	                                scope: this
	                            }
	                        });
	                    },
	                    scope: this
	                },
	                failureCallback: {
	                    fn: function() {console.log("Failed to load nodeRef of Object type dictionary.")},
	                    scope: this
	                }
	            });

				//получить nodeRef справочника "Категория событий"
	            Alfresco.util.Ajax.jsonGet({
	                url: Alfresco.constants.PROXY_URI + "lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(EVENT_CATEGORY),
	                successCallback: {
	                    fn: function(response){
	                        //заполнить выпадающий список типов объектов
	                        Alfresco.util.Ajax.jsonGet({
	                            url: Alfresco.constants.PROXY_URI + "lecm/dictionary/api/getChildrenItems.json?nodeRef=" + response.json.nodeRef,
	                            successCallback: {
	                                fn: function(response){
	                                    var items = response.json;

	                                    if (items) {
	                                        var SELECT_TYPES = {
	                                            defaultIndex: 0,
	                                            options: []
	                                        };

	                                        SELECT_TYPES.options[0] = {
	                                            value: '',
	                                            text: '${msg("label.select.types.all")}'
	                                        };
	                                        for (var i = 0; i < items.length; i++) { // [].forEach() не работает в IE
	                                            var item = items[i];

	                                            SELECT_TYPES.options[i + 1] = {
	                                                value: item.nodeRef,
	                                                text: item.name
	                                            };
	                                        }
	                                        makeSelect('${id}-eventCategories', SELECT_TYPES);
	                                    }
	                                },
	                                scope: this
	                            },
	                            failureCallback: {
	                                fn: function() {console.log("Failed to load Object types list.")},
	                                scope: this
	                            }
	                        });
	                    },
	                    scope: this
	                },
	                failureCallback: {
	                    fn: function() {console.log("Failed to load nodeRef of Object type dictionary.")},
	                    scope: this
	                }
	            });

	            makeSelect('${id}-days', SELECT_DAYS);
	            makeWhoseSelect();

		        YAHOO.util.Event.addListener("${id}-results-container", "scroll", onBusinessJournalContainerScroll);
	        }

		    function onBusinessJournalContainerScroll () {
			    var container = event.currentTarget;
			    if (container.scrollTop + container.clientHeight == container.scrollHeight) {
				    Dom.setStyle("${id}-results-loading", "visibility", "visible");
				    loadBusinessJournalRecords(false);
			    }
	        }

	        function loadBusinessJournalRecords(clearList) {
		        var container = Dom.get('${id}_results');
		        if (clearList) {
			        skipItemsCount = 0;
			        container.scrollTop = 0;
		        }

	            var data = "";
	            var inputs = Selector.query('#${id}_controls input[type=hidden]');

	            for (var i = 0; i < inputs.length; i++) { // [].forEach() не работает в IE
	                var item = inputs[i];

	                data += (i == 0 ? '' : '&') + item.name + '=' + item.value;
	            }
                data += "&maxItems=" + loadItemsCount + "&skipCount=" + skipItemsCount + "&checkMainObject=false";

	            Alfresco.util.Ajax.jsonGet({
	                url: Alfresco.constants.PROXY_URI + "lecm/business-journal/api/search?" + data,
	                successCallback: {
	                    fn: function(response){
	                        var results = response.json;

	                        if (results) {
		                        skipItemsCount += results.length;
		                        if (clearList) {
	                            container.innerHTML = '';
		                        }

	                            if (results.length > 0) {
	                                for (var i = 0; i < results.length; i++) { // [].forEach() не работает в IE
	                                    var item = results[i];
	                                    var div = createRow();
	                                    var detail = document.createElement('span');

	                                    detail.innerHTML = item.record;
	                                    detail.setAttribute('class', 'detail');
	                                    div.appendChild(detail);
	                                    div.innerHTML = div.innerHTML + '<br />' + Alfresco.util.relativeTime(new Date(item.date));
	                                    container.appendChild(div);
	                                }
	                            }
	                        }
		                    Dom.setStyle("${id}-results-loading", "visibility", "hidden");
	                    },
	                    scope: this
	                },
	                failureCallback: {
	                    fn: function() {console.log("Failed to load Business Journal rows.")},
	                    scope: this
	                }
	            });
	        }
	        function makeSelect(inputId, selectData) {
	            var options = selectData.options;
	            var defaultOption = options[selectData.defaultIndex];
	            var hidden = Dom.get(inputId + '-hidden');
	            var onOptionClick = function (p_sType, p_aArgs, p_oItem) {
	                selectButton.set("label", p_oItem.cfg.getProperty("text"));
	                selectButton.set("title", p_oItem.cfg.getProperty("text"));
	                hidden.value = p_oItem.value;
		            loadBusinessJournalRecords(true);
	            };

	            for (var i = 0; i < options.length; i++) { // [].forEach() не работает в IE
	                options[i].onclick = {fn: onOptionClick};
	            }

	            var selectButton = new YAHOO.widget.Button(inputId, {
	                type: "menu",
	                label: defaultOption.text,
	                menu: options
	            });

                selectButton.subscribe("click", function() {
                    setMenuHeight(selectButton.getMenu().id);
                });

	            hidden.value = defaultOption.value;
		        loadBusinessJournalRecords(true);
	        }
	        function makeWhoseSelect() {
	            Alfresco.util.Ajax.jsonGet({
	                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
	                successCallback: {
	                    fn: function(response){
	                        var employee = response.json;

	                        if (employee && employee["is-boss"] == "true") {
	                             SELECT_WHOSE.options.splice(2, 0, DEPARTMENT_OPTION);
	                        }
	                        makeSelect('${id}-whose', SELECT_WHOSE);
	                    },
	                    scope: this
	                },
	                failureCallback: {
	                    fn: function() {
	                        console.log("Failed to load current Employee.");
	                        makeSelect('${id}-whose', SELECT_WHOSE);
	                    },
	                    scope: this
	                }
	            });
	        }
	        function createRow(innerHtml) {
	            var div = document.createElement('div');

	            div.setAttribute('class', 'row');
	            if (innerHtml) {
	                div.innerHTML = innerHtml;
	            }
	            return div;
	        }

	        Event.onDOMReady(init);
	    })();
	    //]]>
	</script>
</#if>

<div class="dashlet business-journal">
    <div class="title">${msg("label.title")}</div>
	<#if hasAccess>
	    <div id="${id}_controls" class="toolbar flat-button">
	        <input type="button" id="${id}-types">
	        <input type="button" id="${id}-eventCategories">
	        <input type="button" id="${id}-days">
	        <input type="button" id="${id}-whose">
	        <input type="hidden" id="${id}-types-hidden" name="type">
	        <input type="hidden" id="${id}-eventCategories-hidden" name="eventCategory">
	        <input type="hidden" id="${id}-days-hidden" name="days">
	        <input type="hidden" id="${id}-whose-hidden" name="whose">
	    </div>
		<div class="body scrollableList" id="${id}-results-container">
			<div id="${id}_results"></div>
			<div id="${id}-results-loading" class="loading-image-container">
				<img src="${url.context}/res/components/images/lightbox/loading.gif">
			</div>
		</div>
	<#else>
		<div class="body scrollableList no-access">${msg("label.no.access")}</div>
	</#if>
</div>
