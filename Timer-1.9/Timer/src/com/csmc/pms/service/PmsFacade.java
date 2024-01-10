/*
 * �������� 2006-8-31
 *
 * @author laol
 */
package com.csmc.pms.service;

import com.csmc.util.mail.MailManager;
import com.csmc.util.mail.MimeMailManager;

public interface PmsFacade {
    public void setMailManager(MailManager mailManager);

    public void setMimeMailManager(MimeMailManager mimeMailManager);

    // Ѳ��δ���ʼ�����(day)
    public void invokeDayUnfinishPcService(String pcStyleName);

    // Ѳ��δ���ʼ�����(week)
    public void invokeWeekUnfinishPcService(String pcStyleName);

    // �ձ�������ʱ����
    public void invokePmDayService();

    // �豸����Խ������
    public void invokeUnscheduleParamOosService();

    // ��������ʱδ������
    public void invokeDelayPmDayService();

    // ����δ���������ѣ������ձ�����
    public void invokeForecastPmDayService();

    // ���ó���һ��������
    public void invokeRequisitionOverdueService();

    // �ؼ�����ʹ�õ�������
    public void KeyPartsUseAlarm();
}
