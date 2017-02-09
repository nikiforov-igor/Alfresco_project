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
          } else {
              doc.properties["lecm-errands:execution-report-status"] = "PROJECT";
              doc.properties["lecm-errands:execution-report"] = doc.properties["lecm-eds-aspect:completion-signal-reason"];
          }
          doc.properties["lecm-errands:execution-report-is-execute"] = true;
      }
      edsDocument.resetChildChangeSignal(doc);
      doc.save();
  }
};