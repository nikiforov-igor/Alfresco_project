(function(){
    /**
     * YUI Library aliases
     */
     var Event = YAHOO.util.Event;
     var Dom = YAHOO.util.Dom;
     var Bubbling = YAHOO.Bubbling;

    //Set block height
    function setTreeHeight() {
        var treeHeight = Dom.getY("lecm-content-ft") - Dom.getY("lecm-content-main");
        var block = Dom.get('orgstructure-tree');
        if (block) {
            Dom.setStyle(block, "position", "inherit");
            Dom.setStyle(block, 'height', treeHeight +'px');
            Dom.setStyle(block, 'width', 'auto');
        }
    }

    function clearHeight() {
        var block = Dom.get('orgstructure-tree');
        if (block) {
            var child = block.childNodes[0];

            if ( parseInt(Dom.getStyle(block, 'height')) > parseInt(Dom.getStyle(child, 'height')) ) {
                Dom.setStyle(block, "height", "auto");
            } else {
                Dom.setStyle(block, "width", parseFloat(Dom.getStyle(block, 'width')) + "px");
                Dom.setStyle(block, "position", "absolute");
            }
        }
    }

    Event.onDOMReady(function() {
        Bubbling.on("GridRendered", setTreeHeight, this);
        Bubbling.on("HeightSetted", setTreeHeight, this);
        Bubbling.on("HeightSetting",clearHeight, this)
    });

})();

