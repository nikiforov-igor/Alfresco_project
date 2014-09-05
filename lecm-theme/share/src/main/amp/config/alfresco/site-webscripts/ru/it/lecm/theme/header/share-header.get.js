<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

var imagesRoot = "/share/components/images/header/"

var logicECMWidgets = [
	{
	    id: "DICTIONARY_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "DICTIONARY_MENU_ITEM",
	        label: "Справочники",
	        targetUrl: "allDictionary",
	        iconImage: imagesRoot + "dictionary_light.png"
	    }
	},
	{
	    id: "ORGSTRUCTURE_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "ORGSTRUCTURE_MENU_ITEM",
	        label: "Оргструктура",
	        targetUrl: "org-employees",
	        iconImage: imagesRoot + "orgstructure_light.png"
	    }
	},
    {
        id: "ORGSTRUCTURE_DICTIONARY_MENU_ITEM",
        name: "alfresco/header/AlfMenuItem",
        config: {
        id: "ORGSTRUCTURE_DICTIONARY_MENU_ITEM",
        label: "Справочник организации",
        targetUrl: "orgstructure-dictionary",
        iconImage: imagesRoot + "orgstructure_light.png"
        }
    },
	{
	    id: "WCALENDAR_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "WCALENDAR_MENU_ITEM",
	        label: "Рабочий календарь",
	        targetUrl: "wcalendar-calendar",
	        iconImage: imagesRoot + "wcalendar_light.png"

	    }
	},
	{
	    id: "DELEGATION_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "DELEGATION_MENU_ITEM",
	        label: "Делегирование",
	        targetUrl: "delegation-list",
	        iconImage: imagesRoot + "delegation_light.png"
	    }
	},
	{
	    id: "DOCUMENTS_JOURNAL_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "DOCUMENTS_JOURNAL_MENU_ITEM",
	        label: "Базовый журнал",
	        targetUrl: "documents-journal",
	        iconImage: imagesRoot + "doc-journal_light.png"
	    }
	},
	{
	    id: "SUBSCRIPTIONS_JOURNAL_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "SUBSCRIPTIONS_JOURNAL_MENU_ITEM",
	        label: "Подписки",
	        targetUrl: "subscriptions-to-type"
	    }
	},
	{
	    id: "BUISNESS_JOURNAL_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "BUISNESS_JOURNAL_MENU_ITEM",
	        label: "Бизнес журнал",
	        targetUrl: "business-journal"
	    }
	},
	{
	    id: "ROUTES_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "ROUTES_MENU_ITEM",
	        label: "Маршруты документов",
	        targetUrl: "routes"
	    }
	}
];

var standartWidgets = [
	{
	    id: "HOME_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "HOME_MENU_ITEM",
	        label: "Домашняя страница",
	        targetUrl: "user/" + encodeURIComponent(user.name) + "/dashboard"
	    }
	},
	{
         id: "PEOPLE_MENU_ITEM",
         name: "alfresco/menus/AlfMenuItem",
         config: {
            id: "PEOPLE_MENU_ITEM",
            label: "Люди",
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
            label: "Репозиторий",
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
          label: "Мои задачи",
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
          label: "Мои процессы",
          iconClass: "alf-myworkflows-icon",
          targetUrl: "my-workflows#filter=workflows|active"
       }
    },
    {
		id: "MY_FILES_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "MY_FILES_MENU_ITEM",
			label: "Мои файлы",
			targetUrl: "context/mine/myfiles"
		}
    },
    {
        id: "USER_MENU_PROFILE_MENU_ITEM",
        name: "alfresco/header/AlfMenuItem",
        config:
        {
           id: "USER_MENU_PROFILE_MENU_ITEM",
           label: "Мой профиль",
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
			label: "Приложение",
			targetUrl: "console/admin-console/application"
		}
	},
	{
		id: "ADMIN_GROUPS_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_GROUPS_MENU_ITEM",
			label: "Группы",
			targetUrl: "console/admin-console/groups"
		}
	},
	{
		id: "ADMIN_REPOSITORY_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_REPOSITORY_MENU_ITEM",
			label: "Репозиторий",
			targetUrl: "console/admin-console/repository"
		}
	},
	{
		id: "ADMIN_USERS_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_USERS_MENU_ITEM",
			label: "Пользователи",
			targetUrl: "console/admin-console/users"
		}
	},
	{
		id: "ADMIN_DOC_MODEL_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_DOC_MODEL_MENU_ITEM",
			label: "Модель документов",
			targetUrl: "doc-model-list"
		}
	},
	{
		id: "ADMIN_REPORTS_EDITOR_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_REPORTS_EDITOR_MENU_ITEM",
			label: "Редактор отчётов",
			targetUrl: "reports-editor"
		}
	},
	{
		id: "ADMIN_ARM_SETTINGS",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_ARM_SETTINGS",
			label: "Настройка АРМ",
			targetUrl: "arm-settings"
		}
	},
	{
		id: "ADMIN_GROUP_ACTIONS",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_GROUP_ACTIONS",
			label: "Групповые операции",
			targetUrl: "group-actions"
		}
	}
];

if (!conditionEditionTeam) {
	adminWidgets.push({
		id: "ADMIN_REPLIACATION_MENU_ITEM",
		name: "alfresco/menus/AlfMenuItem",
		config: {
			id: "ADMIN_REPLIACATION_MENU_ITEM",
			label: "Репликация",
			targetUrl: "console/admin-console/replication-jobs"
		}
	});
}


var moreMenu = {
    id: "LOGIC_ECM_MORE_MENU_BAR",
    name: "alfresco/header/AlfMenuBarPopup",
    config: {
        id: "LOGIC_ECM_MORE_MENU_BAR",
        label: "Ещё...",
        widgets: [
	        {
	            name: "alfresco/menus/AlfMenuGroup",
	            id: "LOGIC_ECM_WIDGETS",
	            config: {
	                label: "Logic ECM",
	                widgets: logicECMWidgets
	            }
	        },
	        {
	            name: "alfresco/menus/AlfMenuGroup",
	            config: {
	                label: "Стандартные",
	                widgets: standartWidgets
	            }
	        },
	        {
	            name: "alfresco/menus/AlfMenuGroup",
	            config: {
	                label: "Мои...",
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
			label: "Администрирование",
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
			label: "Логика СЭД",
			targetUrl: "arm?code=SED"
		}
	},
	{
		id: "NOTIFICATIONS",
		name: "logic_ecm/notifications/NotificationsPopup",
		config: {
			id: "NOTIFICATIONS",
			label: "Уведомления"
		}
	},
	{
		id: "HEADER_SITES_MENU",
		name: "alfresco/header/AlfSitesMenu",
		config: {
			id: "HEADER_SITES_MENU",
			label: "Сайты",
			currentSite: page.url.templateArgs.site,
			currentUser: user.name
		}
	}
];

appItems.push(moreMenu);

var menuBar =
    widgetUtils.findObject(model.jsonModel, "id", "HEADER_APP_MENU_BAR");
if (menuBar != null) {
    menuBar.config.widgets = appItems;
}