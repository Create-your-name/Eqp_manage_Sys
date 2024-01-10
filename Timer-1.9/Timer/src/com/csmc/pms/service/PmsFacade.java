/*
 * 创建日期 2006-8-31
 *
 * @author laol
 */
package com.csmc.pms.service;

import com.csmc.util.mail.MailManager;
import com.csmc.util.mail.MimeMailManager;

public interface PmsFacade {
    public void setMailManager(MailManager mailManager);

    public void setMimeMailManager(MimeMailManager mimeMailManager);

    // 巡检未作邮件提醒(day)
    public void invokeDayUnfinishPcService(String pcStyleName);

    // 巡检未作邮件提醒(week)
    public void invokeWeekUnfinishPcService(String pcStyleName);

    // 日保养表单分时提醒
    public void invokePmDayService();

    // 设备参数越界提醒
    public void invokeUnscheduleParamOosService();

    // 保养表单超时未作提醒
    public void invokeDelayPmDayService();

    // 当日未作保养提醒（不含日保养）
    public void invokeForecastPmDayService();

    // 领用超过一个月提醒
    public void invokeRequisitionOverdueService();

    // 关键备件使用到期提醒
    public void KeyPartsUseAlarm();
}
