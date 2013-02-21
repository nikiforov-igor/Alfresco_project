package ru.it.lecm.statemachine;

public interface StateMachineServiceBean {
	String nextTransition(String taskId);
	String getCurrentTaskId(String executionId);
}