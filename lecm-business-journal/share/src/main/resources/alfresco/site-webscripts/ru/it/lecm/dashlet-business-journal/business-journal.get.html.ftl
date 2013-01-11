<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/dashlets/dashlet-business-journal.css" />
<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var OBJECT_TYPE = "Тип объекта";
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;
        var selects;

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");

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
                                        var typesSelect = Dom.get('${id}_types_select');

                                        typesSelect.options.length = 0;
                                        typesSelect.options[0] = new Option(OBJECT_TYPE, "");
                                        items.forEach(function(item, index) {
                                            typesSelect.options[index + 1] = new Option(item.name, item.nodeRef);
                                        });
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

            selects = Dom.get('${id}_controls').getElementsByTagName('select');
            Event.addListener(selects, 'change', refreshResults);
            refreshResults();
        }
        function refreshResults() {
            var data = {};

            for (var i = 0; i < selects.length; i++) {
                var select = selects[i];
                data[select.name] = select.value;
            }

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

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet business-journal">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollable">
        <div id="${id}_controls" class="controls">
            <form action="" method="post">
                <select id="${id}_types_select" name="type"></select>
                <select name="days">
                    <option value="1">${msg("label.select.days.today")}</option>
                    <option value="2">${msg("label.select.days.2days")}</option>
                    <option value="5" selected="selected">${msg("label.select.days.5days")}</option>
                    <option value="10">${msg("label.select.days.10days")}</option>
                </select>
                <select name="whose">
                    <option value="my">${msg("label.select.whose.my")}</option>
                    <option value="department">${msg("label.select.whose.department")}</option>
                    <option value="control">${msg("label.select.whose.control")}</option>
                    <option value="all" selected="selected">${msg("label.select.whose.all")}</option>
                </select>
            </form>
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