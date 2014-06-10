<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/dashlets/dashlet-notifications.css" />
<@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>


<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
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

            makeSelect('${id}-days', SELECT_DAYS);
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

            for (var i = 0; i < options.length; i++) { // [].forEach() не работает в IE
                options[i].onclick = {fn: onOptionClick};
            }

            var selectButton = new YAHOO.widget.Button(inputId, {
                type: "menu",
                label: defaultOption.text,
                menu: options
            });
            hidden.value = defaultOption.value;
            refreshResults();
        }
        function createRow(innerHtml) {
            var div = document.createElement('div');

            div.setAttribute('class', 'row');
            if (innerHtml) {
                div.innerHTML = innerHtml;
            }
            return div;
        }
        function refreshResults() {
            var data = "";
            var inputs = Selector.query('#${id}_controls input[type=hidden]');

            for (var i = 0; i < inputs.length; i++) { // [].forEach() не работает в IE
                var item = inputs[i];

                data += (i == 0 ? '' : '&') + item.name + '=' + item.value;
            }

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/notifications/search?" + data,
                successCallback: {
                    fn: function(response){
                        var results = response.json;

                        if (results) {
                            var container = Dom.get('${id}_results');
                            container.innerHTML = '';

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
                            } else {
                                container.appendChild(createRow('${msg("label.no.records")}'));
                            }
                        }
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function() {console.log("Failed to load Notification rows.")},
                    scope: this
                }
            });
        }

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet notifications">
    <div class="title">${msg("label.title")}</div>
    <div id="${id}_controls" class="toolbar flat-button">
        <input type="button" id="${id}-days">
        <input type="hidden" id="${id}-days-hidden" name="days">
    </div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>