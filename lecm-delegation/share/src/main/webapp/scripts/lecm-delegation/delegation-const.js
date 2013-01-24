LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Const = LogicECM.module.Delegation.Const || {
	"nodeRef": "", //nodeRef папки в которой хранятся данные с перечнем делегирования
	"itemType": "", //тип данных который отображается в таблице с перечнем делегирования
	"isBoss": false, //является ли текущий пользователь руководителем в каком либо подразделении
	"isEngineer": false, //является ли текущий пользователь технологом
	"hasSubordinate": false //есть ли в пользователя подчиненный (используется только на странице delegation-opts)
};
