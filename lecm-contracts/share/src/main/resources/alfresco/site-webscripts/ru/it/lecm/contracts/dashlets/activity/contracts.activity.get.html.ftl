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
                        var row = createRow();
                        var avatar = document.createElement('div');
                        var img = document.createElement('img');
                        var content = document.createElement('div');
                        var detail = document.createElement('span');

                        img.setAttribute('alt', item.initiator);
                        if (item.initiatorRef && item.initiatorRef != "") {
                            img.setAttribute('src', Alfresco.constants.PROXY_URI + 'lecm/profile/employee-photo?nodeRef=' + item.initiatorRef);
                        } else {
                            img.setAttribute('src', Alfresco.constants.URL_RESCONTEXT + 'components/images/no-user-photo-64.png');
                        }
                        avatar.setAttribute('class', 'avatar');
                        avatar.appendChild(img);
                        detail.setAttribute('class', 'detail');
                        detail.innerHTML = item.record;
                        content.setAttribute('class', 'content');
                        content.appendChild(detail);
                        content.innerHTML = content.innerHTML + '<br />' + Alfresco.util.relativeTime(new Date(item.date));
                        row.appendChild(avatar);
                        row.appendChild(content);
                        container.appendChild(row);
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