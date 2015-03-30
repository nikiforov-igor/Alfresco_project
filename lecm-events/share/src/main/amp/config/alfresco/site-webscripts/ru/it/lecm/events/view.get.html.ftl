<@markup id="css" >
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/jquery/fullcalendar/fullcalendar.css" group="calendar"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/taglibrary/taglibrary.css" group="calendar"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/jquery/jquery-ui-1.8.11.custom.min.js" group="calendar"/>
   <@script type="text/javascript" src="${url.context}/res/jquery/fullcalendar/fullcalendar.js" group="calendar"/>
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
   </@>
</@>