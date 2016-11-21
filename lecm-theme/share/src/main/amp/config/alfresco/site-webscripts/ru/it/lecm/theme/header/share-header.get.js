<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

var imagesRoot = "/share/components/images/header/";

function getArmsForMenu() {
	var result = [],
		url = "/lecm/menu/arms/get",
		response = remote.connect("alfresco").get(url);

	if (response.status == 200) {
		result = eval('(' + response + ')');
	}
	return result;
}

var logicECMWidgets = [];
var arms = getArmsForMenu();

for (var i = 0; i < arms.length; i++) {
	var msgId = "label." + arms[i].code + ".menu.item";
    var title = msg.get(msgId);
	if (title == msgId) {
		title = arms[i].title;
	}
	logicECMWidgets.push({
		id: arms[i].code + "_MENU_ITEM_ADDITIONAL",
		name: "alfresco/header/AlfMenuItem",
		config: {
			id: arms[i].code + "_MENU_ITEM_ADDITIONAL",
			label: title,
			targetUrl: "arm?code=" + arms[i].code,
			iconClass: "arm-menu-item " + arms[i].code
		}
	});
}

logicECMWidgets.push({
	id: "ORGSTRUCTURE_DICTIONARY_MENU_ITEM",
	name: "alfresco/header/AlfMenuItem",
	config: {
		id: "ORGSTRUCTURE_DICTIONARY_MENU_ITEM",
		label: msg.get("label.orgstructure-dictionary"),
		targetUrl: "orgstructure-dictionary",
		iconImage: imagesRoot + "orgstructure_light.png"
	}
});

logicECMWidgets.push({
	id: "ADMINISTRATION_MENU_ITEM",
	name: "alfresco/header/AlfMenuItem",
	config: {
		id: "ADMINISTRATION_MENU_ITEM",
		label: msg.get("label.admin.page"),
		targetUrl: "admin",
		iconImage: imagesRoot + "settings_16_light.png"
	}
});

var standartWidgets = [
	{
	    id: "PEOPLE_MENU_ITEM",
	    name: "alfresco/menus/AlfMenuItem",
	    config: {
	       id: "PEOPLE_MENU_ITEM",
	       label: msg.get("label.people"),
	       targetUrl: "people-finder"
	    }
    }
];

if (user.isAdmin || showRepositoryLink == "true") {
	standartWidgets.push({
         id: "REPOSITORY_MENU_ITEM",
         name: "alfresco/menus/AlfMenuItem",
         config: {
            id: "REPOSITORY_MENU_ITEM",
            label: msg.get("label.repository"),
            targetUrl: "repository"
         }
    });
}

var myWidgets = [
	{
       id: "MY_TASKS_MENU_ITEM",
       name: "alfresco/header/AlfMenuItem",
       config:
       {
          id: "MY_TASKS_MENU_ITEM",
          label: msg.get("label.my-tasks"),
          iconClass: "alf-mytasks-icon",
          targetUrl: "my-tasks#filter=workflows|active"
       }
    },
    {
       id: "MY_WORKFLOWS_MENU_ITEM",
       name: "alfresco/header/AlfMenuItem",
       config:
       {
          id: "MY_WORKFLOWS_MENU_ITEM",
          label: msg.get("label.my-workflows"),
          iconClass: "alf-myworkflows-icon",
          targetUrl: "my-workflows#filter=workflows|active"
       }
    },
    {
		id: "MY_FILES_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "MY_FILES_MENU_ITEM",
			label: msg.get("label.my-files"),
			targetUrl: "context/mine/myfiles"
		}
    },
    {
        id: "USER_MENU_PROFILE_MENU_ITEM",
        name: "alfresco/header/AlfMenuItem",
        config:
        {
           id: "USER_MENU_PROFILE_MENU_ITEM",
           label: msg.get("label.my-profile"),
           iconClass: "alf-user-profile-icon",
           targetUrl: "user/" + encodeURIComponent(user.name) + "/profile"
        }
    }
];

var adminWidgets = [
	{
		id: "ADMIN_CONSOLE_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_CONSOLE_MENU_ITEM",
			label: msg.get("label.application"),
			targetUrl: "console/admin-console/application"
		}
	},
	{
		id: "ADMIN_GROUPS_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_GROUPS_MENU_ITEM",
			label: msg.get("label.groups"),
			targetUrl: "console/admin-console/groups"
		}
	},
	{
		id: "ADMIN_REPOSITORY_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_REPOSITORY_MENU_ITEM",
			label: msg.get("label.admin-repository"),
			targetUrl: "console/admin-console/repository"
		}
	},
	{
		id: "ADMIN_USERS_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_USERS_MENU_ITEM",
			label: msg.get("label.users"),
			targetUrl: "console/admin-console/users"
		}
	},
	{
		id: "ADMIN_DOC_MODEL_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_DOC_MODEL_MENU_ITEM",
			label: msg.get("label.documents.model"),
			targetUrl: "doc-model-list"
		}
	},
	{
		id: "ADMIN_REPORTS_EDITOR_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_REPORTS_EDITOR_MENU_ITEM",
			label: msg.get("label.reports.editor"),
			targetUrl: "reports-editor"
		}
	},
	{
		id: "ADMIN_ARM_SETTINGS",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_ARM_SETTINGS",
			label: msg.get("label.arm.settings"),
			targetUrl: "arm-settings"
		}
	},
	{
		id: "ADMIN_GROUP_ACTIONS",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_GROUP_ACTIONS",
			label: msg.get("label.group.actions"),
			targetUrl: "group-actions"
		}
	}
];

//if (!conditionEditionTeam) {
//	adminWidgets.push({
//		id: "ADMIN_REPLIACATION_MENU_ITEM",
//		name: "alfresco/menus/AlfMenuItem",
//		config: {
//			id: "ADMIN_REPLIACATION_MENU_ITEM",
//			label: msg.get("label.replication"),
//			targetUrl: "console/admin-console/replication-jobs"
//		}
//	});
//}


var moreMenu = {
    id: "LOGIC_ECM_MORE_MENU_BAR",
    name: "alfresco/header/AlfMenuBarPopup",
    config: {
        id: "LOGIC_ECM_MORE_MENU_BAR",
        label: msg.get("label.more"),
        widgets: [
	        {
	            name: "alfresco/menus/AlfMenuGroup",
	            id: "LOGIC_ECM_WIDGETS",
	            config: {
	                label:  msg.get("label.logic-ecm.menu-group"),
	                widgets: logicECMWidgets
	            }
	        },
	        {
	            name: "alfresco/menus/AlfMenuGroup",
	            config: {
	                label: msg.get("label.commons.menu-group"),
	                widgets: standartWidgets
	            }
	        },
	        {
	            name: "alfresco/menus/AlfMenuGroup",
	            config: {
	                label: msg.get("label.my.menu-group"),
	                widgets: myWidgets
	            }
	        }
        ]
    }
};

if (user.isAdmin){
	moreMenu.config.widgets.push({
		name: "alfresco/menus/AlfMenuGroup",
		config: {
			label: msg.get("label.administration.menu-group"),
			widgets: adminWidgets
		}
	});
}

var appItems = [
	{
		id: "SED_MENU_ITEM",
		name: "alfresco/menus/AlfMenuBarItem",
		config: {
			id: "SED_MENU_ITEM",
			label: msg.get("label.SED.menu.item"),
			targetUrl: "arm?code=SED"
		}
	},
	{
		id: "HOME_MENU_ITEM",
		name: "alfresco/menus/AlfMenuBarItem",
		config: {
			id: "HOME_MENU_ITEM",
			label: msg.get("label.home.page"),
			targetUrl: "user/" + encodeURIComponent(user.name) + "/dashboard"
		}
	},
	{
		id: "HEADER_SITES_MENU",
		name: "alfresco/header/AlfSitesMenu",
		config: {
			id: "HEADER_SITES_MENU",
			label: msg.get("label.sites.menu"),
			currentSite: page.url.templateArgs.site,
			currentUser: user.name
		}
	},
	moreMenu,
	{
		id: "NOTIFICATIONS",
		name: "logic_ecm/notifications/NotificationsPopup",
		config: {
			id: "NOTIFICATIONS",
			label: msg.get("label.notifications.menu-item")
		}
	}
];

var menuBar =
    widgetUtils.findObject(model.jsonModel, "id", "HEADER_APP_MENU_BAR");
if (menuBar != null) {
    menuBar.config.widgets = appItems;
}