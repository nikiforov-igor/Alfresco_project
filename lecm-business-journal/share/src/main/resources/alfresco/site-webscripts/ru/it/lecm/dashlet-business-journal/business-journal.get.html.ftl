<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/dashlets/dashlet-business-journal.css" />
<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var OBJECT_TYPE = "Тип объекта";
        var SELECT_DAYS = {
            defaultIndex: 0,
            options: [
                {
                    value: '',
                    text: '${msg("label.select.all")}'
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
                },
                {
                    value: 'department',
                    text: '${msg("label.select.whose.department")}'
                },
                {
                    value: 'control',
                    text: '${msg("label.select.whose.control")}'
                }
            ]
        };
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
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
                url: Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(OBJECT_TYPE),
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
                                        items.forEach(function(item, index) {
                                            SELECT_TYPES.options[index + 1] = {
                                                value: item.nodeRef,
                                                text: item.name
                                            };
                                        });

                                        makeSelect('${id}-types', SELECT_TYPES);
                                        refreshResults();
                                    }
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function() {alert("Failed to load Object types list.")},
                                scope: this
                            }
                        });
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function() {alert("Failed to load nodeRef of Object type dictionary.")},
                    scope: this
                }
            });

            makeSelect('${id}-days', SELECT_DAYS);
            makeSelect('${id}-whose', SELECT_WHOSE);
            refreshResults();
        }
        function refreshResults() {
            var data = {};
            var inputs = Selector.query('#${id}_controls input[type=hidden]');

            inputs.forEach(function(item, i) {
                data[item.name] = item.value;
            });

//            console.log(data);
            console.log('refresh');
            return; //todo TEMP!
            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + "", //todo
                dataObj: data,
                successCallback: {
                    fn: function(response){
                        var results = response.json;

                        if (results) {
                            var container = Dom.get('${id}_results');

                            container.innerHTML = '';
                            results.forEach(function(item, i) {
                                var div = document.createElement('div');

                                div.innerHTML = item;
                                container.appendChild(div);
                            });
                        }
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function() {alert("Failed to load Business Journal rows.")},
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
                hidden.value = p_oItem.value;
                refreshResults();
            };

            options.forEach(function(o, i) {
                o.onclick = {fn: onOptionClick};
            });

            var selectButton = new YAHOO.widget.Button(inputId, {
                type: "menu",
                label: defaultOption.text,
                menu: options
            });
            hidden.value = defaultOption.value;
        }

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet business-journal">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollable">
        <div id="${id}_controls" class="controls flat-button">
            <input type="button" id="${id}-types">
            <input type="button" id="${id}-days">
            <input type="button" id="${id}-whose">
            <input type="hidden" id="${id}-types-hidden" name="type">
            <input type="hidden" id="${id}-days-hidden" name="days">
            <input type="hidden" id="${id}-whose-hidden" name="whose">
        </div>
        <div id="${id}_results" class="results">
            <div class="row">
                <a>Иванов И.</a> создал документ № <a>01-28/15</a> 5 часов назад
            </div>
            <div class="row">
                <a>Иванов И.</a> создал документ № <a>01-28/15</a> 5 часов назад
            </div>
            <div class="row">
                <a>Иванов И.</a> создал документ № <a>01-28/15</a> 5 часов назад
            </div>

        </div>
    </div>
</div>