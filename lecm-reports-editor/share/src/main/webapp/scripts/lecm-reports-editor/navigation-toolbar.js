(function () {
    var Dom = YAHOO.util.Dom;
    LogicECM.module.ReportsEditor.NavigationBar = function (htmlId) {
        return LogicECM.module.ReportsEditor.NavigationBar.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.NavigationBar", htmlId, ["button", "container", "connection"]);
    };
    YAHOO.extend(LogicECM.module.ReportsEditor.NavigationBar, Alfresco.component.Base,
        {
            options: {
                previousButton: false,
                nextButton: false,
                previousPage: "",
                nextPage: ""
            },
            reportId: null,
            footerButtons: {
            },

            onReady: function () {
                this._initButtons();

                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            _initButtons: function () {
                this.footerButtons.previous = Alfresco.util.createYUIButton(this, "prevPageButton", this._onPrevPage, {
                    disabled: !this.options.previousButton
                });

                this.footerButtons.next = Alfresco.util.createYUIButton(this, "nextPageButton", this._onNextPage, {
                    disabled: !this.options.nextButton
                });
            },

            _onNextPage: function () {
                var context = this;
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + this.options.nextPage + "?reportId=" + context.reportId;
            },

            _onPrevPage: function () {
                console.log(this);
                var context = this;
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + this.options.previousPage + "?reportId=" + context.reportId;
            },

            setReportId: function (reportId) {
                this.reportId = reportId;
            }

        }, true);

})();