var this_ExpertsUtil = this;

var ExpertsUtil =
{
    getExperts:function getExperts(nodeRef) {
        if (nodeRef) {
            var url = '/lecm/experts/main?nodeRef=' + nodeRef;
            var result = remote.connect("alfresco").get(url);

            if (result.status == 200) {
                return eval('(' + result + ')');
            }
        }
        return [];
    }
};
