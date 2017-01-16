<@markup id="css" >
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/jquery/fullcalendar/fullcalendar.css" group="calendar"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/jquery/fullcalendar/fullcalendar.css" group="calendar"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-events/viewmore.css" group="calendar"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-events/formbubble.css" group="calendar"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/third-party/jquery-ui-1.10.3.custom.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/jquery/fullcalendar/fullcalendar.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/jquery.formbubble.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/fullcalendar.viewmore.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-calendar-view.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-calendar-view-fullCalendar.js" group="calendar"/>
</@>

<@markup id="widgets">
   <@createWidgets group="calendar"/>
   <@inlineScript group="calendar">
      Alfresco.util.addMessages(${messages}, "Alfresco.EventInfo");
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
      <![endif]-->
      <input id="yui-history-field" type="hidden" />

      <div id="${el}Container" class="alf-calendar fullCalendar">
          <div id="${el}View">
          </div>
      </div>

      <#assign  viewFormId = el + "Container-viewForm"/>
      <div id="${viewFormId}" class="yui-panel hidden1 event-dialog">
          <div id="${viewFormId}-head" class="hd">${msg("logicecm.view")}</div>
          <div id="${viewFormId}-body" class="bd">
              <div class="right-part">
                  <ul class="event-actions">
                      <li id="${viewFormId}-action-accept" class="event-accept"><a href="#">Принять</a></li>
                      <li id="${viewFormId}-action-reject" class="event-reject"><a href="#">Отклонить</a></li>
                      <li class="event-more"><a id="${viewFormId}-action-more" href="#">Подробнее</a></li>
                      <li id="${viewFormId}-action-edit-button" class="event-edit"><a id="${viewFormId}-action-edit" href="#">Редактировать</a></li>
                      <li id="${viewFormId}-action-delete" class="event-remove"><a href="#">Удалить</a></li>
                  </ul>
              </div>
              <div id="${viewFormId}-content" class="center-part"></div>
          </div>
      </div>
   </@>
</@>