(function(){
    /**
     * YUI Library aliases
     */
        Event = YAHOO.util.Event;


    //Set block height
    function setTreeHeight() {
        var treeHeight = Dom.getY("lecm-content-ft") - Dom.getY("lecm-content-main");
        var block = Dom.get('orgstructure-tree');
        var child = block.childNodes[0];
        Dom.setStyle(block, "position", "inherit");
        Dom.setStyle(block, 'height', treeHeight +'px');
        Dom.setStyle(block, 'width', 'auto');
    }

    function clearHeight() {
        var block = Dom.get('orgstructure-tree');
        var child = block.childNodes[0];

        if ( parseInt(Dom.getStyle(block, 'height')) > parseInt(Dom.getStyle(child, 'height')) ) {
            Dom.setStyle(block, "height", "auto");
        } else {
            Dom.setStyle(block, "width", parseFloat(Dom.getStyle(block, 'width')) + "px");
            Dom.setStyle(block, "position", "absolute");
        }
    }
    OrgstructureUnit = function()
    {
        YAHOO.Bubbling.on("GridRendered", setTreeHeight, this);
        YAHOO.Bubbling.on("HeightSetted", setTreeHeight, this);
        YAHOO.Bubbling.on("HeightSetting",clearHeight, this);
        return this;
    };

    Event.onDOMReady(function() {
        var org = OrgstructureUnit();
    });

})();

