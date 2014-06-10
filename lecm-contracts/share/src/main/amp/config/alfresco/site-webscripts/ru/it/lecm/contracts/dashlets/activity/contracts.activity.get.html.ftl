<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/contracts-activity.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
</@>
<@markup id="html">
	<#assign id = args.htmlid>
	<#assign containerId = id + "-container">
	
	<div class="dashlet contracts-activity bordered">
	    <div class="title dashlet-title">
	        <span>${msg("label.title")}</span>
	    </div>
	    <div class="body scrollableList dashlet-body" id="${id}_results">
	    ${msg("label.no-more.records")}
	    </div>
	    <script type="text/javascript">
	        //<![CDATA[
	        (function(){
		        var Dom = YAHOO.util.Dom,
		            Event = YAHOO.util.Event;
		        var reqUrl = 'lecm/business-journal/api/search?type=' + "${refs}" + "&days=30&whose=&checkMainObject=true";
		        var createRow = function(innerHtml) {
		            var div = document.createElement('div');
		
		            div.setAttribute('class', 'row');
		            if (innerHtml) {
		                div.innerHTML = innerHtml;
		            }
		            return div;
		        };
				var messageFlag = false;
				var pageCount = 50;
				var startIndex = 0;
		
		        var loadRecords = function() {
		            var me = this;
		            Alfresco.util.Ajax.request(
		                    {
		                        method: "GET",
		                        url:Alfresco.constants.PROXY_URI + reqUrl + "&skipCount=" + startIndex + "&maxItems=" + pageCount,
		                        requestContentType: "application/json",
		                        responseContentType: "application/json",
		                        successCallback:{
		                            fn:function getRecords(response){
		                                records = response.json;
		                                if (records) {
		                                    var container = Dom.get('${id}_results');
		
		                                                if (records.length > 0) {
		                                                    if(!messageFlag)
		                                                        container.innerHTML = '';
		                                                    messageFlag = true;
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
		                                                    pageCount = 0;
		                                                }
		                                            }
		                                          }
		
		
		                                       },
		                                       failureCallback:{
													fn:function (response) {
													Alfresco.util.PopupManager.displayMessage(
													{
														text: "${msg("label.no-more.records")}"
													});
								},
								scope:this
							}
						});
				};
		
		
		
		        function init() {
		
					loadRecords();
		            YAHOO.util.Event.addListener('${id}_results', "scroll", onContainerScroll, this);
		        }
		
		        var onContainerScroll = function (event, scope) {
		            if (pageCount == 0) {
		                return;
		            }
		            var container = event.currentTarget;
		            if (container.scrollTop + container.clientHeight == container.scrollHeight) {
		                startIndex += pageCount;
		                loadRecords();
		            }
		        };
		
		        Event.onDOMReady(init);
	        })();
	        //]]>
	    </script>
	</div>
</@>