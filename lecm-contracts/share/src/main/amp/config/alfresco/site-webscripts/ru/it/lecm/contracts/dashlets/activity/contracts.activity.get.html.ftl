<#assign id = args.htmlid>

<div class="dashlet contracts-activity bordered">
	<div class="title dashlet-title">
		<span>${msg("label.title")}</span>
	</div>
	<div class="body scrollableList dashlet-body" id="${id}_results">
		<span>${msg("label.no-more.records")}</span>
	</div>
</div>

<script type="text/javascript">
(function() {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		messageFlag = false,
		pageCount = 50,
		startIndex = 0;


	LogicECM.module.Base.Util.loadCSS(['css/lecm-contracts/contracts-activity.css'], init);

	function init() {
		loadRecords();
	}

	function createRow(innerHtml) {
		var div = document.createElement('div');

		div.setAttribute('class', 'row');
		if (innerHtml) {
			div.innerHTML = innerHtml;
		}
		return div;
	}

	function onContainerScroll(event, scope) {
		if (pageCount == 0) {
			return;
		}
		var container = event.currentTarget;
		if (container.scrollTop + container.clientHeight == container.scrollHeight) {
			startIndex += pageCount;
			loadRecords();
		}
	}

	function loadRecords() {
		var template = '{proxyUri}lecm/business-journal/api/search?type={refs}&days={days}&checkMainObject={checkMainObject}&skipCount={skipCount}&maxItems={maxItems}&whose=';
		var url = YAHOO.lang.substitute(template, {
			proxyUri: Alfresco.constants.PROXY_URI,
			refs: '${refs}',
			days: 30,
			checkMainObject: 'true',
			skipCount: startIndex,
			maxItems: pageCount
		});
		var successCallback = {
			scope: this,
			fn: function (serverResponse) {
				var container, i, item, row, avatar, img, content, detail,
					records = serverResponse.json;

				if (records) {
					YAHOO.util.Event.addListener('${id}_results', 'scroll', onContainerScroll, this);

					if (records.length) {
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
					} else {
						pageCount = 0;
					}
				}
			}
		};

		Alfresco.util.Ajax.jsonGet({
			url: url,
			successCallback: successCallback,
			failureMessage: '${msg("label.no-more.records")}'
		});
	}
})();
</script>
