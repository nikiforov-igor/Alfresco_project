var ExecuteErrandScript = {

  processChildExecutedSignal: function (doc) {
      var children = [];
      var childrenErrands = errands.getChildErrands(doc.nodeRef.toString());
      var childrenResolutions = errands.getChildResolutions(doc.nodeRef.toString());
      if (childrenErrands) {
          children = children.concat(childrenErrands);
      }
      if (childrenResolutions) {
          children = children.concat(childrenResolutions);
      }
      var completionReason = doc.properties["lecm-eds-aspect:completion-signal-reason"];
      var isProcessExecutedChild = children.some(function (child) {
          var isStatusOk = child.properties["lecm-statemachine:status"] == "Исполнено";
          var isAutoClose = child.properties["lecm-errands:auto-close"];
          var isCompleteReasonOk = child.properties["lecm-errands:execution-report"] == completionReason;
          return isStatusOk && isAutoClose && isCompleteReasonOk;
      });
      if (isProcessExecutedChild) {
          if (doc.properties["lecm-errands:execution-report-status"] == "PROJECT") {
              doc.properties["lecm-errands:execution-report"] += '<p>' + doc.properties["lecm-eds-aspect:completion-signal-reason"] + '</p>';
          } else if (!doc.properties["lecm-errands:execution-report-status"]) {
              doc.properties["lecm-errands:execution-report-status"] = "PROJECT";
              doc.properties["lecm-errands:execution-report"] = doc.properties["lecm-eds-aspect:completion-signal-reason"];
          }
          ExecuteErrandScript.executeErrand(doc, false);
      }
      edsDocument.resetChildChangeSignal(doc);
      doc.save();
  },
  executeErrand: function (document, closeChild){
      var reportRequired  = document.properties["lecm-errands:report-required"];
      var currentUser = orgstructure.getCurrentEmployee();
      var recipients = [];
      var notificationTemplate = null;
      var author = null;
      var authorAssoc = document.assocs["lecm-errands:initiator-assoc"];
      if (authorAssoc && authorAssoc.length == 1) {
          author = authorAssoc[0];
      }
      var controller = null;
      var controllerAssoc = document.assocs["lecm-errands:controller-assoc"];
      if (controllerAssoc && controllerAssoc.length == 1) {
          controller = controllerAssoc[0];
      }
      if (closeChild) {
          var reason = "Завершено исполнением поручения-основания ";
          reason += documentScript.wrapperDocumentLink(document, '{lecm-document:present-string}');
          var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
          var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
          childrenErrands.forEach(function (childErrand) {
              if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                  edsDocument.sendCompletionSignal(childErrand, reason, currentUser);
              }
          });
          childrenResolutions.forEach(function (childResolution) {
              if (!statemachine.isFinal(childResolution.nodeRef.toString()) && !statemachine.isDraft(childResolution)) {
                  edsDocument.sendCompletionSignal(childResolution, reason, currentUser);
              }
          });
      }
      if (!reportRequired) {
          document.properties["lecm-errands:execution-report-status"] = "ACCEPT";
          document.properties["lecm-errands:execute-result"] = "executed";
          notificationTemplate = "ERRANDS_EXECUTED_WITHOUT_REPORT";
          if (author) {
              recipients.push(author);
          }
          if (controller) {
              recipients.push(controller);
          }
          var additionalDoc = errands.getAdditionalDocument(document.nodeRef.toString());
          if (additionalDoc) {
              additionalDoc.properties["lecm-eds-aspect:completion-signal-reason"] = document.properties["lecm-errands:execution-report"];
              additionalDoc.save();
          }
      } else {
          document.properties["lecm-errands:execution-report-status"] = "ONCONTROL";
          var reportRecipientType = document.properties["lecm-errands:report-recipient-type"];
          notificationTemplate = "ERRANDS_EXECUTED_WITH_REPORT";
          if (reportRecipientType == "AUTHOR" && author) {
              recipients = [author];
          } else if (reportRecipientType == "CONTROLLER" && controller) {
              recipients = [controller];
          } else if (reportRecipientType == "AUTHOR_AND_CONTROLLER") {
              if (author) {
                  recipients.push(author);
              }
              if (controller) {
                  recipients.push(controller);
              }
          }
          document.properties["lecm-errands:execute-result"] = "onControl";
      }
      notifications.sendNotificationFromCurrentUser({
          recipients: recipients,
          templateCode: notificationTemplate,
          templateConfig: {
              mainObject: document,
              eventExecutor: currentUser
          }
      });
      document.properties["lecm-errands:project-report-ref"] = null;
      document.properties["lecm-errands:project-report-text"] = null;
      document.properties["lecm-errands:project-report-attachment"] = null;
      document.properties["lecm-errands:project-report-connections"] = null;
  }
};