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
     * @return 返回 scheduleIndex。
     */
    public long getScheduleIndex() {
        return scheduleIndex;
    }

    /**
     * @param scheduleIndex 要设置的 scheduleIndex。
     */
    public void setScheduleIndex(long scheduleIndex) {
        this.scheduleIndex = scheduleIndex;
    }
    
    /**
     * @return 返回 equipmentId。
     */
    public String getEquipmentId() {
        return equipmentId;
    }

    /**
     * @param equipmentId 要设置的 equipmentId。
     */
    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
    
    /**
     * @return 返回 periodIndex。
     */
    public long getPeriodIndex() {
        return periodIndex;
    }

    /**
     * @param periodIndex 要设置的 periodIndex。
     */
    public void setPeriodIndex(long periodIndex) {
        this.periodIndex = periodIndex;
    }

    /**
     * @return 返回 scheduleEvent。
     */
    public String getScheduleEvent() {
        return scheduleEvent;
    }

    /**
     * @param scheduleEvent 要设置的 scheduleEvent。
     */
    public void setScheduleEvent(String scheduleEvent) {
        this.scheduleEvent = scheduleEvent;
    }
    
    /**
     * @return 返回 scheduleDate。
     */
    public Date getScheduleDate() {
        return scheduleDate;
    }

    /**
     * @param scheduleDate 要设置的 scheduleDate。
     */
    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    /**
     * @return 返回 scheduleNote。
     */
    public String getScheduleNote() {
        return scheduleNote;
    }

    /**
     * @param scheduleNote 要设置的 scheduleNote。
     */
    public void setScheduleNote(String scheduleNote) {
    	if(scheduleNote == null) {
    		scheduleNote = "";
    	}
        this.scheduleNote = scheduleNote;
    }
        
    /**
     * @return 返回 creator。
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的 creator。
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    /**
     * @return 返回 eventIndex。
     */
    public long getEventIndex() {
        return eventIndex;
    }

    /**
     * @param eventIndex 要设置的 eventIndex。
     */
    public void setEventIndex(long eventIndex) {
        this.eventIndex = eventIndex;
    }
    
    /**
     * @return 返回 periodName。
     */
    public String getPeriodName() {
        return periodName;
    }

    /**
     * @param periodName 要设置的 periodName。
     */
    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }
    
    /**
     * @return 返回 equipmentEngineer。
     */
    public String getEquipmentEngineer() {
        return equipmentEngineer;
    }

    /**
     * @param equipmentEngineer 要设置的 equipmentEngineer。
     */
    public void setEquipmentEngineer(String equipmentEngineer) {
    	if(equipmentEngineer == null) {
    		equipmentEngineer = "";
    	}
        this.equipmentEngineer = equipmentEngineer;
    }
    
    /**
     * @return 返回 sectionLeader。
     */
    public String getSectionLeader() {
        return sectionLeader;
    }

    /**
     * @param sectionLeader 要设置的 sectionLeader。
     */
    public void setSectionLeader(String sectionLeader) {
    	if(sectionLeader == null) {
    		sectionLeader = "";
    	}
        this.sectionLeader = sectionLeader;
    }
}
