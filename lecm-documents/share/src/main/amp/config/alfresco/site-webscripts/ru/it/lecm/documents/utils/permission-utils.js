const PERM_DOC_CREATE = "_lecmPerm_DocCreate";
const PERM_DOC_DELETE = "_lecmPerm_DocDelete";
const PERM_ATTR_LIST = "_lecmPerm_AttrList";
const PERM_INNER_ATTR_LIST = "_lecmPerm_InnerAttrList";
const PERM_ATTR_EDIT = "_lecmPerm_AttrEdit";
const PERM_CONTENT_LIST = "_lecmPerm_ContentList";
const PERM_CONTENT_ADD = "_lecmPerm_ContentAdd";
const PERM_CONTENT_VIEW = "_lecmPerm_ContentView";
const PERM_CONTENT_ADD_VER = "_lecmPerm_ContentAddVer";
const PERM_CONTENT_DELETE = "_lecmPerm_ContentDelete";
const PERM_OWN_CONTENT_DELETE = "_lecmPerm_OwnContentDelete";
const PERM_CONTENT_COPY = "_lecmPerm_ContentCopy";
const PERM_WF_LIST = "_lecmPerm_WFEnumBP";
const PERM_WF_TASK_LIST = "_lecmPerm_WFTaskList";
const PERM_HISTORY_VIEW = "_lecmPerm_HistoryView";
const PERM_TAG_VIEW = "_lecmPerm_TagView";
const PERM_TAG_CREATE = "_lecmPerm_TagCreate";
const PERM_TAG_DELETE = "_lecmPerm_TagDelete";
const PERM_LINKS_VIEW = "_lecmPerm_LinksView";
const PERM_LINKS_CREATE = "_lecmPerm_LinksCreate";
const PERM_LINKS_DELETE = "_lecmPerm_LinksDelete";
const PERM_COMMENT_CREATE = "_lecmPerm_CommentCreate";
const PERM_COMMENT_VIEW = "_lecmPerm_CommentView";
const PERM_COMMENT_DELETE = "_lecmPerm_CommentDelete";
const PERM_ACTION_EXEC = "_lecmPerm_ActionExec";
const PERM_MEMBERS_LIST = "_lecmPerm_MemberList";
const PERM_MEMBERS_ADD = "_lecmPerm_MemberAdd";
const PERM_SET_RATE = "_lecmPerm_SetRate";
const PERM_READ_ATTACHMENT = "_lecmPerm_ReadAttachment";

function hasPermission(nodeRef, permission) {
    if (nodeRef == null || permission == null) {
        return false;
    }
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=' + permission;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var perm = eval('(' + result + ')');
    return (("" + perm) ==  "true");
}

function hasOnlyInDraftPermission(nodeRef, permissionGroup) {
    if (nodeRef == null || permissionGroup == null) {
        return false;
    }
    var url = '/lecm/security/api/getOnlyDraftPermission?nodeRef=' + nodeRef + '&permission=' + permissionGroup;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var perm = eval('(' + result + ')');
    return (("" + perm) ==  "true");
}

function hasRole(roleId) {
	if (roleId == null) {
		return false;
	}
	var url = '/lecm/orgstructure/isCurrentEmployeeHasBusinessRole?roleId=' + roleId;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var hasRole = eval('(' + result + ')');
	return (("" + hasRole) ==  "true");
}

function hasStatemachine(nodeRef) {
    url = '/lecm/documents/hasStatemachine?nodeRef=' + nodeRef;
    result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var hasStatemachine = eval('(' + result + ')');
        return hasStatemachine;
    } else {
        return false;
    }
}

function hasReadAttachmentPermission(nodeRef, userId) {
	var args = 'nodeRef=' + nodeRef + '&aspect=lecm-document-aspects:lecm-attachment' + '&permission=' + PERM_READ_ATTACHMENT + '&user=' + userId;
    url = '/lecm/documents/isEmpHasPermToReadAttachment?' + args;
    result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var hasPermission = eval('(' + result + ')');
        return hasPermission;
    } else {
        return false;
    }
}

function isStarter(docType) {
	var url = '/lecm/documents/employeeIsStarter?docType=' + docType;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var perm = eval('(' + result + ')');
	return (("" + perm) == "true");
}