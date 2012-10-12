(function(){var o=Alfresco.util.encodeHTML,c=YAHOO.util.Dom,x=YAHOO.util.Event,t=YAHOO.util.Selector,g=Alfresco.util.fromISO8601,j=Alfresco.util.toISO8601,p=Alfresco.thirdparty.dateFormat,f=YAHOO.widget.DateMath;YAHOO.lang.augmentObject(Alfresco.CalendarView.prototype,{initAgendaEvents:function u(){var y=c.getElementsByClassName("agendaNav");c.removeClass(y,"hidden");x.addListener(y,"click",this.bind(this.onLoadEvents))},renderCellStart:function a(C,A,D,F){var B=A.getData(),z="",E=B.displayStart||B.start,y=B.displayEnd||B.end;if(B.isAllDay){z=this.msg("label.all-day")}else{z=E+"-"+y}C.innerHTML=z},renderCellName:function q(C,A,D,E){var B=A.getData(),y=this.getRel(B),z="";z='<a href="'+B.uri+'" rel="'+y+'" class="summary">'+B.name+"</a>";C.innerHTML=z},renderCellDescription:function r(B,z,C,D){var A=z.getData(),y=this.truncate(A);B.innerHTML=o(y)},renderCellLocation:function b(B,z,C,D){var A=z.getData(),y="";y='<span class="agendaLocation">'+A.where+"</span>";if(A.where===""){c.addClass(B,"empty")}B.innerHTML=y},renderCellActions:function k(E,I,C,y){var A=I.getData(),B="",z=[],G=this.getRel(A),F='<a href="'+A.uri+'" class="{type}" title="{tooltip}" rel="'+G+'"><span>{label}</span></a>',H=false,D=this;H=this.options.permitToCreateEvents;if(H){z.push(YAHOO.lang.substitute(F,{type:"deleteAction",label:D.msg("agenda.action.delete.label"),tooltip:D.msg("agenda.action.delete.tooltip")}));z.push(YAHOO.lang.substitute(F,{type:"editAction",label:D.msg("agenda.action.edit.label"),tooltip:D.msg("agenda.action.edit.tooltip")}))}z.push(YAHOO.lang.substitute(F,{type:"infoAction summary",label:D.msg("agenda.action.info.label"),tooltip:D.msg("agenda.action.info.tooltip")}));B=z.join(" ");E.innerHTML=B},renderEvents:function h(z){var A=0,C=this.getCalendarContainer(),N={},P=this.options.tag||null,D={},K=[],B='<a href="" class="addEvent">',G="</a>",E=this.options.id+"-noEvent",J='<div id="'+E+'" class="noEvent"><p class="instructionTitle">{noevents}</p></div>',F='<div id="'+E+'" class="noEvent"><p class="instructionTitle">{noevents}</p><span>{link}</span></div>';this.widgets.Data=this.widgets.Data||{};N=this.widgets.Data;if(z){z=this.filterMultiday(z);this.events=z}else{z=this.events}z=this.tagFilter(z);A=z.length;this.updateTitle();for(var I=0;I<A;I++){var H=z[I],L=H.displayFrom||H.from;L=L.split("T")[0];D[L]=D[L]||{events:[]};D[L].events.push(H)}for(L in N){if(!D[L]){this.removeDay(L);delete N[L]}}if(A>0){var y=c.get(E);if(y){y.parentNode.removeChild(y)}for(L in D){var O=false;if(N[L]&&N[L].dataTable){if(YAHOO.lang.JSON.stringify(N[L].events)!=YAHOO.lang.JSON.stringify(D[L].events)){O=true;N[L].events=D[L].events}}else{O=true;N[L]=D[L]}if(O){this.renderDay(L)}}}else{var M=(this.options.permitToCreateEvents)?F:J;C.innerHTML=YAHOO.lang.substitute(M,{link:this.msg("agenda.add-events",B,G),noevents:this.msg("agenda.no-events")})}if(!this.eventsInitialised){this.initAgendaEvents();this.eventsInitialised=true}if(this.loadingLabelBuffer&&this.loadingEl){this.loadingEl.innerHTML=this.loadingLabelBuffer;this.loadingEl=this.loadingLabelBuffer=null}},renderDay:function e(B){var D=this.widgets.Data[B],C=[{key:"start",formatter:this.bind(this.renderCellStart)},{key:"name",formatter:this.bind(this.renderCellName)},{key:"description",formatter:this.bind(this.renderCellDescription)},{key:"where",formatter:this.bind(this.renderCellLocation)},{key:"actions",formatter:this.bind(this.renderCellActions)}],G=this.getCalendarContainer();if(!D||!this.isValidDateForView(g(B))){return false}D.dataSource=new YAHOO.util.LocalDataSource(D.events);if(D.dataTable){this.removeDay(B)}var E=document.createElement("div"),z=document.createElement("h2"),y=c.getChildren(G),H=g(B).getTime(),A,F=Alfresco.util.toISO8601(new Date()).split("T")[0];E.id=this.options.id+"-dt-"+B;z.id=this.options.id+"-head-"+B;c.addClass(z,"dayTitle");z.innerHTML=Alfresco.util.friendlyDate(g(B),this.msg("date-format.dayDateMonth"));c.setAttribute(z,"title",p(g(B),this.msg("date-format.fullDate")));if(B===F){c.addClass(E,"theme-bg-color-2")}for(i in y){if(g(y[i].id.slice(-10)).getTime()>H){A=y[i];break}}if(A){c.insertBefore(z,A);c.insertBefore(E,A)}else{G.appendChild(z);G.appendChild(E)}D.dataTable=new YAHOO.widget.DataTable(E,C,D.dataSource,{onEventHighlightRow:function(I,J){c.addClass(J,"yui-dt-highlight")}});D.dataTable.subscribe("rowMouseoverEvent",D.dataTable.onEventHighlightRow);D.dataTable.subscribe("rowMouseoutEvent",D.dataTable.onEventUnhighlightRow)},removeDay:function s(y){var z=t.query("[id$="+y+"]");for(i in z){var A=c.get(z[i]);A.parentNode.removeChild(A)}},onLoadEvents:function d(B){if(!this.loadingEl){var z=30,y=24*60*60*1000;var A=x.getTarget(B);this.loadingLabelBuffer=A.innerHTML;this.loadingEl=A;this.loadingEl.innerHTML=this.msg("message.loading");if(YAHOO.util.Selector.test(this.loadingEl,"a.previousEvents")){this.options.startDate=new Date(this.options.startDate.getTime()-(z*y))}else{if(YAHOO.util.Selector.test(this.loadingEl,"a.nextEvents")){this.options.endDate=new Date(this.options.endDate.getTime()+(z*y))}}this.getEvents()}x.preventDefault(B)},getCalendarContainer:function m(){return c.get(this.options.id)},updateTitle:function n(){var y=this.options.startDate,C=this.options.endDate,B="",A=this.msg("date-format.longDate"),D=this.msg("date-format.longDateNoYear"),z=p(C,A);if(y.getFullYear()===C.getFullYear()){B=p(y,D)}else{B=p(y,A)}this.titleEl.innerHTML=this.msg("title.agenda",B,z);tagTitleEl=c.getElementsByClassName("tagged","span",this.titleEl);if(tagTitleEl.length>1){this.titleEl.removeChild(tagTitleEl[0])}if(this.options.tag){tagTitleEl=Alfresco.CalendarHelper.renderTemplate("taggedTitle",{taggedWith:this.msg("label.tagged-with"),tag:this.options.tag});this.titleEl.appendChild(tagTitleEl)}},truncate:function w(C,B){var F=this.msg("agenda.truncate.show-more"),z=this.msg("agenda.truncate.ellipsis"),D=parseInt(B)||parseInt(this.options.truncateLength)||100,E=C.description,y=E,A="";if(E.length>D+F.length+z.length){y=E.substring(0,D);A=y.replace(/\w+$/,"");y=(A.length>0)?A:y;y='<span class="truncatedText">'+y+z+' <a href="'+C.uri+'" rel="'+this.getRel(C)+'" class="showMore">'+F+"</a>."}return y},expandDescription:function l(y){var z=this.getEventObj(y),B=y.parentNode,C=z.description,A='<a href="'+z.uri+'" rel="'+y.rel+'" class="showLess">'+this.msg("agenda.truncate.show-less")+"</a>";B.innerHTML=o(C)+" "+A},collapseDescription:function v(y){var z=this.getEventObj(y),A=y.parentNode;A.innerHTML=o(this.truncate(z))}},true)})();