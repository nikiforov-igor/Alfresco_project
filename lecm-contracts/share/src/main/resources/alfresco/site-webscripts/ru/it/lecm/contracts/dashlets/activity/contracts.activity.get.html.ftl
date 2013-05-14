<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<div class="dashlet contracts-activity bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
        Здесь будет последняя активность...
    </div>
    <script type="text/javascript">
        //<![CDATA[
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;
        var records = ${records};
        var createRow = function(innerHtml) {
            var div = document.createElement('div');

            div.setAttribute('class', 'row');
            if (innerHtml) {
                div.innerHTML = innerHtml;
            }
            return div;
        };

        function init() {
            if (records) {
                var container = Dom.get('${id}_results');
                container.innerHTML = '';

                if (records.length > 0) {
                    for (var i = 0; i < records.length; i++) { // [].forEach() не работает в IE
                        var item = records[i];
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
        }

        Event.onDOMReady(init);
        //]]>
    </script>
</div>