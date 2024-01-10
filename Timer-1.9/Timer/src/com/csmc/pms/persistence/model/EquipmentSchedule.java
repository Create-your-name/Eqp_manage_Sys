package com.csmc.pms.persistence.model;

import java.sql.Date;

public class EquipmentSchedule {
	private long scheduleIndex;
	
	private String equipmentId;
    
    private long periodIndex;   
    
    private String scheduleEvent;
    
    private Date scheduleDate;
    
    private String scheduleNote;
    
    private String creator;
    
    private long eventIndex;
    
    private String periodName;
    
    private String equipmentEngineer;
    
    private String sectionLeader;

    /**
     * @return ���� scheduleIndex��
     */
    public long getScheduleIndex() {
        return scheduleIndex;
    }

    /**
     * @param scheduleIndex Ҫ���õ� scheduleIndex��
     */
    public void setScheduleIndex(long scheduleIndex) {
        this.scheduleIndex = scheduleIndex;
    }
    
    /**
     * @return ���� equipmentId��
     */
    public String getEquipmentId() {
        return equipmentId;
    }

    /**
     * @param equipmentId Ҫ���õ� equipmentId��
     */
    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    /**
     * @return ���� periodIndex��
     */
    public long getPeriodIndex() {
        return periodIndex;
    }

    /**
     * @param periodIndex Ҫ���õ� periodIndex��
     */
    public void setPeriodIndex(long periodIndex) {
        this.periodIndex = periodIndex;
    }

    /**
     * @return ���� scheduleEvent��
     */
    public String getScheduleEvent() {
        return scheduleEvent;
    }

    /**
     * @param scheduleEvent Ҫ���õ� scheduleEvent��
     */
    public void setScheduleEvent(String scheduleEvent) {
        this.scheduleEvent = scheduleEvent;
    }
    
    /**
     * @return ���� scheduleDate��
     */
    public Date getScheduleDate() {
        return scheduleDate;
    }

    /**
     * @param scheduleDate Ҫ���õ� scheduleDate��
     */
    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    /**
     * @return ���� scheduleNote��
     */
    public String getScheduleNote() {
        return scheduleNote;
    }

    /**
     * @param scheduleNote Ҫ���õ� scheduleNote��
     */
    public void setScheduleNote(String scheduleNote) {
    	if(scheduleNote == null) {
    		scheduleNote = "";
    	}
        this.scheduleNote = scheduleNote;
    }
        
    /**
     * @return ���� creator��
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator Ҫ���õ� creator��
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    /**
     * @return ���� eventIndex��
     */
    public long getEventIndex() {
        return eventIndex;
    }

    /**
     * @param eventIndex Ҫ���õ� eventIndex��
     */
    public void setEventIndex(long eventIndex) {
        this.eventIndex = eventIndex;
    }
    
    /**
     * @return ���� periodName��
     */
    public String getPeriodName() {
        return periodName;
    }

    /**
     * @param periodName Ҫ���õ� periodName��
     */
    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }
    
    /**
     * @return ���� equipmentEngineer��
     */
    public String getEquipmentEngineer() {
        return equipmentEngineer;
    }

    /**
     * @param equipmentEngineer Ҫ���õ� equipmentEngineer��
     */
    public void setEquipmentEngineer(String equipmentEngineer) {
    	if(equipmentEngineer == null) {
    		equipmentEngineer = "";
    	}
        this.equipmentEngineer = equipmentEngineer;
    }
    
    /**
     * @return ���� sectionLeader��
     */
    public String getSectionLeader() {
        return sectionLeader;
    }

    /**
     * @param sectionLeader Ҫ���õ� sectionLeader��
     */
    public void setSectionLeader(String sectionLeader) {
    	if(sectionLeader == null) {
    		sectionLeader = "";
    	}
        this.sectionLeader = sectionLeader;
    }
}
