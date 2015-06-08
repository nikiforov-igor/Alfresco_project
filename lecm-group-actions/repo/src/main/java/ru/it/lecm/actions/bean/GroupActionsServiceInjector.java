/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.actions.bean;

import java.util.List;

/**
 *
 * @author ikhalikov
 */
public class GroupActionsServiceInjector {
	private List<String> aspects;
    private GroupActionsService groupActionsService;

    public void setAspects(List<String> aspects) {
        this.aspects = aspects;
    }

    public void setGroupActionsService(GroupActionsService groupActionsService) {
        this.groupActionsService = groupActionsService;
    }

    public void init() {
        groupActionsService.getAspects().addAll(aspects);
    }
}
