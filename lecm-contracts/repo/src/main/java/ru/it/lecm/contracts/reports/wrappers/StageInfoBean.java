package ru.it.lecm.contracts.reports.wrappers;

import java.util.Date;

/**
 * User: dbashmakov
 * Date: 21.10.13
 * Time: 13:46
 */
public class StageInfoBean {
    private String name;
    private Date startDate;
    private Date endDate;
    private String status;
    private String responsibleName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsibleName() {
        return responsibleName;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }
}
