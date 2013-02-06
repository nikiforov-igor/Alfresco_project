package ru.it.lecm.businessjournal.beans;

/**
 * @author dbashmakov
 *         Date: 05.02.13
 *         Time: 17:11
 */
public interface EventCategory {

	public static final String ADD = "ADD";
	public static final String EDIT = "EDIT";
	public static final String DELETE = "DELETE";
	public static final String ADD_NEW_VERSION = "ADD_NEW_VERSION";
	public static final String TAKE_JOB_POSITION = "TAKE_JOB_POSITION";
	public static final String RELEASE_JOB_POSITION = "RELEASE_JOB_POSITION";
	public static final String ACCEPT_DELEGATION = "ACCEPT_DELEGATION";
	public static final String CANCEL_DELEGATION = "CANCEL_DELEGATION";
	public static final String TAKE_BOSS_POSITION = "TAKE_BOSS_POSITION";
	public static final String RELEASE_BOSS_POSITION = "RELEASE_BOSS_POSITION";
	public static final String MAKE_POSITION_PRIMARY = "MAKE_POSITION_PRIMARY";
	public static final String START_ABSENCE_ON_WORK = "START_ABSENCE_ON_WORK";
	public static final String FINISH_ABSENCE_ON_WORK = "FINISH_ABSENCE_ON_WORK";
	public static final String CHANGE_DOCUMENT_STATUS = "CHANGE_DOCUMENT_STATUS";
}
