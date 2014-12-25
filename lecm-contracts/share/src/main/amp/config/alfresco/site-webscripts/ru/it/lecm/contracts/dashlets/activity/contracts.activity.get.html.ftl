<#assign id = args.htmlid>

<div class="dashlet contracts-activity bordered">
	<div class="title dashlet-title">
		<span>${msg("label.title")}</span>
	</div>
	<div class="body scrollableList dashlet-body" id="${id}_results">
		<div class="no-records"><span>${msg("label.no-more.records")}</span></div>
	</div>
</div>

<script type="text/javascript">
(function() {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		messageFlag = false;

	LogicECM.module.Base.Util.loadCSS(['css/lecm-contracts/contracts-activity.css'], loadRecords);

	function createRow(innerHtml) {
		var div = document.createElement('div');

		div.setAttribute('class', 'row');
		if (innerHtml) {
			div.innerHTML = innerHtml;
		}
		return div;
	}

	function loadRecords() {
		var successCallback = {
			scope: this,
			fn: function (serverResponse) {
				var container, i, item, row, avatar, img, content, detail,
					records = serverResponse.json;

				if (records) {
					container = Dom.get('${id}_results');
					if(!messageFlag) {
						container.innerHTML = '';
						messageFlag = true;
					}
					for (i in records) {
						item = records[i];
						row = createRow();
						avatar = document.createElement('div');
						img = document.createElement('img');
						content = document.createElement('div');
						detail = document.createElement('span');

						img.setAttribute('alt', item.initiator);
						if (item.initiatorRef) {
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
						content.innerHTML = content.innerHTML + '<br/>' + Alfresco.util.relativeTime(new Date(item.date));
						row.appendChild(avatar);
						row.appendChild(content);
						container.appendChild(row);
					}
				}
			}
		};

		Alfresco.util.Ajax.jsonGet({
			url: Alfresco.constants.PROXY_URI + "lecm/contracts/dashlet/getRecentActivity",
			successCallback: successCallback,
			failureMessage: '${msg("label.no-more.records")}'
		});
	}
})();
</script>
