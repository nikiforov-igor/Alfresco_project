package ru.it.lecm.businessjournal.beans;

/**
 * @author dbashmakov
 *         Date: 05.02.13
 *         Time: 17:11
 */
public interface EventCategory {

	String ADD = "ADD";
	String EDIT = "EDIT";
	String DELETE = "DELETE";
	String ADD_NEW_VERSION = "ADD_NEW_VERSION";
	String TAKE_JOB_POSITION = "TAKE_JOB_POSITION";
	String RELEASE_JOB_POSITION = "RELEASE_JOB_POSITION";
	String ACCEPT_DELEGATION = "ACCEPT_DELEGATION";
	String CANCEL_DELEGATION = "CANCEL_DELEGATION";
	String TAKE_BOSS_POSITION = "TAKE_BOSS_POSITION";
	String RELEASE_BOSS_POSITION = "RELEASE_BOSS_POSITION";
	String MAKE_POSITION_PRIMARY = "MAKE_POSITION_PRIMARY";
	String START_ABSENCE_ON_WORK = "START_ABSENCE_ON_WORK";
	String FINISH_ABSENCE_ON_WORK = "FINISH_ABSENCE_ON_WORK";
	String CHANGE_DOCUMENT_STATUS = "CHANGE_DOCUMENT_STATUS";
	String ADD_STAFF_POSITION = "ADD_STAFF_POSITION";
	String REMOVE_STAFF_POSITION = "REMOVE_STAFF_POSITION";
	String ADD_GROUP_ROLE = "ADD_GROUP_ROLE";
	String REMOVE_GROUP_ROLE = "REMOVE_GROUP_ROLE";
	String TAKE_GROUP_ROLE= "TAKE_GROUP_ROLE";
	String RELEASE_GROUP_ROLE = "RELEASE_GROUP_ROLE";
	String CREATE_DOCUMENT_CONNECTION = "CREATE_DOCUMENT_CONNECTION";
	String DELETE_DOCUMENT_CONNECTION = "DELETE_DOCUMENT_CONNECTION";
}
