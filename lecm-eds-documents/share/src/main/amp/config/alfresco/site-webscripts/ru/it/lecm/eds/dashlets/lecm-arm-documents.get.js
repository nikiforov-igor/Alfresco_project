var url = '/lecm/eds/dashlet/sed-in-work/getSettings';
var result = remote.connect("alfresco").get(url);
if (result.status == 200) {
    model.settings = eval('(' + result + ')');
} else {
    model.settings = {
        baseQuery:'',
        dashletTitle: '',
        title:'',
        isExist:false,
        filters:[]
    };
}
