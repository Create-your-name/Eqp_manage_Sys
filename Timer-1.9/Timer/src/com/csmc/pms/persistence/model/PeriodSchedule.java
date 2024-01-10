package com.csmc.pms.persistence.model;

import java.sql.Date;

public class PeriodSchedule {

    private long scheduleIndex;

    private long periodIndex;

    private String scheduleEvent;

    private Date scheduleDate;

    private String scheduleNote;

    private String creator;

    private long eventIndex;

    private long pcStyleIndex;

    private String mailAddress;// creator's mail

    private String pcStyleName;

    /**
     * @return ???? scheduleIndex??
     */
    public long getScheduleIndex() {
        return scheduleIndex;
    }

    /**
     * @param scheduleIndex ?????? scheduleIndex??
     */
    public void setScheduleIndex(long scheduleIndex) {
        this.scheduleIndex = scheduleIndex;
    }

    /**
     * @return ???? periodIndex??
     */
    public long getPeriodIndex() {
        return periodIndex;
    }

    /**
     * @param periodIndex ?????? periodIndex??
     */
    public void setPeriodIndex(long periodIndex) {
        this.periodIndex = periodIndex;
    }

    /**
     * @return ???? scheduleEvent??
     */
    public String getScheduleEvent() {
        return scheduleEvent;
    }

    /**
     * @param scheduleEvent ?????? scheduleEvent??
     */
    public void setScheduleEvent(String scheduleEvent) {
        this.scheduleEvent = scheduleEvent;
    }

    /**
     * @return ???? scheduleDate??
     */
    public Date getScheduleDate() {
        return scheduleDate;
    }

    /**
     * @param scheduleDate ?????? scheduleDate??
     */
    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    /**
     * @return ???? scheduleNote??
     */
    public String getScheduleNote() {
        return scheduleNote;
    }

    /**
     * @param scheduleNote ?????? scheduleNote??
     */
    public void setScheduleNote(String scheduleNote) {
        if (scheduleNote == null) {
            scheduleNote = "";
        }
        this.scheduleNote = scheduleNote;
    }

    /**
     * @return ???? creator??
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator ?????? creator??
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return ???? eventIndex??
     */
    public long getEventIndex() {
        return eventIndex;
    }

    /**
     * @param eventIndex ?????? eventIndex??
     */
    public void setEventIndex(long eventIndex) {
        this.eventIndex = eventIndex;
    }

    /**
     * @return ???? pcStyleIndex??
     */
    public long getPcStyleIndex() {
        return pcStyleIndex;
    }

    /**
     * @param pcStyleIndex ?????? pcStyleIndex??
     */
    public void setPcStyleIndex(long pcStyleIndex) {
        this.pcStyleIndex = pcStyleIndex;
    }

    /**
     * @return ???? mailAddress??
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * @param mailAddress ?????? mailAddress??
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public void setPcStyleName(String pcStyleName) {
        this.pcStyleName = pcStyleName;
    }

    public String getPcStyleName() {
        return pcStyleName;
    }
}
