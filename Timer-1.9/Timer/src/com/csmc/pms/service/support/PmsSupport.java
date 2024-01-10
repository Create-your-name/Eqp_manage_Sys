/*
 * 创建日期 2007-9-7
 *
 * @author dinghh
 */
package com.csmc.pms.service.support;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.csmc.asura.exception.AsuraException;
import com.csmc.db.DataSourceFactory;
import com.csmc.db.SQLProcess;
import com.csmc.pms.persistence.iface.AccountDao;
import com.csmc.pms.persistence.model.EquipmentSchedule;
import com.csmc.pms.persistence.model.PeriodSchedule;
import com.csmc.util.Log;
import com.csmc.util.mail.MailManager;
import com.csmc.util.mail.MimeMailManager;

public abstract class PmsSupport extends PromisSupport {
	public final static String module = PmsSupport.class.getName();

	/**
	 * 检查巡检未作，发邮件提醒
	 *
	 * @param mailManager
	 * @param pcStyleName
	 * @param unFinishedPc
	 * @return
	 */
	protected void checkUnFinishedPc(MailManager mailManager,
			String pcStyleName, List unFinishedPc) {
		String sendTo = null;
		String sendCc = null;
		String subject = "今天您有如下巡检未作，请尽快完成";
		String body ="";
		String creator = "";
		String mailAddress = "";

		try {
			for(int i = 0 ; i < unFinishedPc.size(); i++) {
				PeriodSchedule periodSchedule = (PeriodSchedule)unFinishedPc.get(i);

				//索引为0，或者当schedule.getCreator不等于临时变量creator时
				if(i == 0 || !periodSchedule.getCreator().equals(creator)) {
					//当索引>0，且creator不同，则进行邮件发送
					if(i != 0) {
						sendTo = mailAddress;
						mailManager.sendMail(sendTo, sendCc, body, subject);
						Log.logInfo("checkUnFinishedPc send mail success[" + sendTo + "/" + periodSchedule.getPcStyleName() + "]", this.getClass().getName());
					}
					//creator重新赋值
					creator = periodSchedule.getCreator();
					mailAddress = periodSchedule.getMailAddress();
					body = "您好!今天您有如下保养未作,请尽快完成!谢谢合作!\n";
				}

				//当schedule.getCreator等于creator时，将该schedule拼接mailBody，待发送
				if(creator.equals(periodSchedule.getCreator())) {
					body += "日期: " + periodSchedule.getScheduleDate() + "\t"
					+ "巡检: " + periodSchedule.getPcStyleName() + "\n";
				}

				//最后一个集合发送
				if(i == unFinishedPc.size() - 1) {
					sendTo = mailAddress;
					mailManager.sendMail(sendTo, sendCc, body, subject);
					Log.logInfo("checkUnFinishedPc send mail success[" + sendTo + "/" + periodSchedule.getPcStyleName() + "]", this.getClass().getName());
				}
			}
		} catch(Exception e) {
			mailManager.sendMail("liuhai82@rxgz.crmicro.com", "liuhai82@rxgz.crmicro.com", "error" + e.getMessage(), "checkUnFinishedPc error");
			Log.logError(e.getMessage(), this.getClass().getName());
		}
	}

	/**
	 * 检查日保养未作，发邮件提醒
	 *
	 * @param mailManager
	 * @param unFinishedPm
	 * @param accountDao
	 * @return
	 */
	protected void checkUnFinishedPm(MailManager mailManager,
			List unFinishedPm, AccountDao accountDao) {
		String sendTo = null;
		String sendCc = null;
		String subject = null;
		String body = null;

		for (Iterator it = unFinishedPm.iterator(); it.hasNext();) {
			EquipmentSchedule equipmentSchedule = (EquipmentSchedule) it.next();
			Log.logInfo("checkUnFinishedPc ["
					+ equipmentSchedule.getScheduleIndex() + "]", this
					.getClass().getName());

			try {

				subject = equipmentSchedule.getEquipmentId() + "日保养未作，请尽快完成- 设备[" + equipmentSchedule.getEquipmentId() + "],PM周期[" + equipmentSchedule.getPeriodName() + "]：今日需做保养表单";

				sendTo = accountDao.getMailByGh(equipmentSchedule
						.getSectionLeader());
				sendTo = sendTo
						+ accountDao.getMailByGh(equipmentSchedule
								.getEquipmentEngineer());

				// sendTo = sendTo + "dinghh@csmc.crmicro.com";
				body = "设备[" + equipmentSchedule.getEquipmentId() + "] \n"
						+"日期: " + equipmentSchedule.getScheduleDate() + "\n"
						+ "保养: " + equipmentSchedule.getPeriodName();

				mailManager.sendMail(sendTo, sendCc, body, subject);
			} catch (DataAccessException e) {
				e.printStackTrace();
				Log.logError("checkUnFinishedPc [ScheduleIndex="
						+ equipmentSchedule.getScheduleIndex() + "]", this
						.getClass().getName());
			}
		}
	}

	protected void checkOutOfMaxUnscheduleParam(MailManager mailManager,
			AccountDao accountDao) throws SQLException, AsuraException {
		List paramlist = queryOutOfMaxUnscheduleParam();
		if (paramlist != null && paramlist.size() > 0) {
			for (Iterator it = paramlist.iterator(); it.hasNext();) {
				Map paramInfo = (Map) it.next();
				String eqpId = (String) paramInfo.get("EQUIPMENT_ID");
				String paramName = (String) paramInfo.get("PARAM_NAME");
				String newStatus = (String) paramInfo.get("EQP_STATUS");
				String value = (String) paramInfo.get("VALUE");
				String maxValue = (String) paramInfo.get("MAX_VALUE");
				String eqpEnger = (String) paramInfo.get("EQUIPMENT_ENGINEER");
				String leader = (String) paramInfo.get("SECTION_LEADER");

				// call tp获得当前状态
				boolean isChange = true;
				String currentStatus = "";
				try {
					currentStatus = this.queryEqpStatus(eqpId);
				} catch (Exception e) {
					Log.logError("getEqpStatus[" + eqpId + "] error ["
							+ e.getMessage() + "]", this.getClass().getName());
					isChange = false;
				}

				if (currentStatus.indexOf("03-") != -1) {
					isChange = false;
				}
				// 修改设备状态
				if (isChange) {
					try {
						this.changeEqpStatus(eqpId, newStatus);
						Log.logInfo("ChangeEqpStatus[" + eqpId + ","
								+ newStatus + "] success", this.getClass()
								.getName());
					} catch (Exception e) {
						Log.logError("ChangeEqpStatus[" + eqpId + "] error ["
								+ e.getMessage() + "]", this.getClass()
								.getName());
					}

				}

				// send mail
				String body = "设备[" + eqpId + "]\n" + "参数[" + paramName + "]\n"
						+ "实际值为[" + value + "]，超过最大值[" + maxValue
						+ "]，故系统自动更改PROMIS设备状态为 " + newStatus + "\n";
				String subject = "PMS自动更改Unschedule超规范设备[" + eqpId + "]状态为["
						+ newStatus + "]";
				String to = accountDao.getMailByGh(eqpEnger);
				String cc = accountDao.getMailByGh(leader);
				mailManager.sendMail(to, cc, body, subject);
				// mailManager.sendMail("laol@csmc.com.cn", "laol@csmc.com.cn",
				// "shaoaj@csmc.com.cn", null, subject, body);
			}
		}
	}

	/**
	 * 查询超MaxValue的不定期参数
	 *
	 * @return
	 * @throws SQLException
	 */
	protected List queryOutOfMaxUnscheduleParam() throws SQLException {
		// String sql =
		// "select equipment_id,param_name,eqp_status,max_value,value from unschedule_eqp_param t where (sort=0 or sort is null) and value > max_value"
		// ;
		String sql = "select a.equipment_id, param_name, eqp_status, max_value, value,"
				+ " b.equipment_engineer,c.section_leader"
				+ " from unschedule_eqp_param a,equipment b,equipment_section c"
				+ " where (sort = 0 or sort is null)"
				+ " and value > max_value"
				+ " and a.equipment_id = b.equipment_id"
				+ " and b.section = c.section";
		List list = SQLProcess.excuteSQLQuery(sql,
				DataSourceFactory.PMS_DATASOURCE);
		return list;
	}

	/**
	 * 3，qinchao 检查MSA设备保养是否到提醒天数,是则发邮件。
	 * 4，2014-5-14 dinghh 备件寿命，按课别报警
	 * 5，2015-11-23 dinghh 菜单备份，提前10天报警一次，到期后天天报警。
	 * @param mailManager
	 * @param accountDao
	 * @param PM
	 * @return
	 * @throws SQLException
	 */
	protected void checkDelayPM(MailManager mailManager, AccountDao accountDao) {
//		//1.校验今日将要到期的保养，明日将超期SendMail
//        List alarmlist = queryDelayPmForAlarm();
//        if(alarmlist.size() > 0 ) {
//            for(Iterator it = alarmlist.iterator(); it.hasNext(); ) {
//                Map map = (Map) it.next();
//                String eqpId = (String) map.get("EQUIPMENT_ID");
//                //String periodIndex = (String) map.get("PERIOD_INDEX");
//                String periodName = (String) map.get("PERIOD_NAME");
//                //String nextPmDate = (String) map.get("NEXT_PM_DATE");
//	            String eqpEnger = (String) map.get("EQUIPMENT_ENGINEER");
//	            String sectionLeader = (String) map.get("SECTION_LEADER");
//	            String section = (String) map.get("SECTION");
//
//	            String isUpdatePromis = (String) map.get("IS_UPDATE_PROMIS");
//                String newStatus = (String) map.get("EQP_STATUS");
//
//                //              send mail
//                String body = "设备[" + eqpId + "]\n" + "PM周期[" + periodName + "]\n" ;
//                if (isUpdatePromis.equals("Y")) {
//                    body = body + "系统明日将自动更改MES设备状态为 " + newStatus + "\n";
//                }
//                String subject = "明日PM超时未作提醒[" + eqpId + "]周期为[" + periodName + "]";
//                String sendTo = accountDao.getMailByGh(sectionLeader);
//                //String sendCc = accountDao.getMailByGh(eqpEnger);
//                String sendCc = accountDao.getMailByGh(eqpEnger)+ accountDao.getMailByGh("11066838");
//                //如果是MSA设备,则使用equipment表中msa_email的地址
//                if (periodName.startsWith("MSA")) {
//                    sendTo = (String) map.get("MSA_EMAIL");
////                    sendCc = accountDao.getMailByGh("11066838");
//                    //sendCc = accountDao.getMailByGh("11065337") + accountDao.getMailByGh("11065541");
//                }
//                mailManager.sendMail(sendTo, sendCc, body, subject);
//                Log.logInfo("send mail success [" + eqpEnger + "/" + section + "/" + eqpId + "/" + periodName + "/" + sendCc + "]", this.getClass().getName());
//                //Log.logInfo("sendCc [" + sendCc + "]", this.getClass().getName());
//            }
//        }
//
//
//		//2.昨日已到期的，今日已超期修改状态，SendMail
//		List list = queryDelayPM();
//		if (list != null && list.size() > 0) {
//			for (Iterator it = list.iterator(); it.hasNext();) {
//				Map map = (Map) it.next();
//				String eqpId = (String) map.get("EQUIPMENT_ID");
//				// String periodIndex = (String) map.get("PERIOD_INDEX");
//				String periodName = (String) map.get("PERIOD_NAME");
//				// String nextPmDate = (String) map.get("NEXT_PM_DATE");
//				String eqpEnger = (String) map.get("EQUIPMENT_ENGINEER");
//				String sectionLeader = (String) map.get("SECTION_LEADER");
//				String section = (String) map.get("SECTION");
//
//				String isUpdatePromis = (String) map.get("IS_UPDATE_PROMIS");
//				String newStatus = (String) map.get("EQP_STATUS");
//
//				// send mail
//				String body = "设备[" + eqpId + "]\n" + "PM周期[" + periodName
//						+ "]\n";
//				// if (isUpdatePromis.equals("Y")) {
//				// body = body + "系统自动更改PROMIS设备状态为 " + newStatus + "\n";
//				// }
//				String subject = "PM超时未作提醒[" + eqpId + "]周期为[" + periodName
//						+ "]";
//				String sendTo = accountDao.getMailByGh(eqpEnger);
//				String sendCc = accountDao.getMailByGh(sectionLeader);
//
//				//如果是MSA设备,则使用equipment表中msa_email的地址
//                if (periodName.startsWith("MSA")) {
//                    sendTo = (String) map.get("MSA_EMAIL");
////                    sendCc = accountDao.getMailByGh("11066838");
//
//                }
//
//				mailManager.sendMail(sendTo, sendCc, body, subject);
//				Log.logInfo("send mail success [" + eqpEnger + "/" + section + "/" + eqpId + "/" +
//				periodName + "/" + sendCc + "]", this.getClass().getName());
//			}
//		}

		// 3. 检查MSA设备保养是否到提醒天数,是则发邮件
        List alertList = queryMsaPmForAlert();
        if (alertList.size() > 0) {
            // 当前日期
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            for (Iterator it = alertList.iterator(); it.hasNext();) {
                Map map = (Map) it.next();
                String eqpId = (String) map.get("EQUIPMENT_ID");
                String msaEmail = (String) map.get("MSA_EMAIL");
                String scheduleDate = (String) map.get("SCHEDULE_DATE");
                String periodName = (String) map.get("PERIOD_NAME");
                String periodDesc = (String) map.get("PERIOD_DESC");
                int defaultDays = Integer.valueOf(
                        (String) map.get("DEFAULT_DAYS")).intValue();
                Log.logInfo("检查MSA设备保养 [" + eqpId + "/" + defaultDays + "/" + scheduleDate + "/" + periodName + "/" + msaEmail + "/" + periodDesc + "]", this.getClass().getName());

                try {
                    // schedule date
                    Date _d_scheduleDate = df.parse(scheduleDate);
                    Calendar _c_scheduleDate = Calendar.getInstance();
                    _c_scheduleDate.setTime(_d_scheduleDate);

                    // compared date
                    Calendar _c_comparedDate = Calendar.getInstance();

                    // 周期大于等于15天的,提前一星期
                    if (defaultDays >= 15 && defaultDays < 60) {
                        _c_comparedDate.add(Calendar.DAY_OF_YEAR, 12);
                    }
                    // 周期两个月或四个月的,提前两星期
                    else if (defaultDays >= 60 && defaultDays < 365) {
                        _c_comparedDate.add(Calendar.DAY_OF_YEAR, 14);
                    }
                    // 周期一年的,提前一个月
                    else if (defaultDays >= 365) {
                        _c_comparedDate.add(Calendar.DAY_OF_YEAR, 30);
                    }
                    Log.logInfo("预定日期："+df.format(_c_scheduleDate.getTime())+"/"+df.format(_c_comparedDate.getTime()), this.getClass().getName());
                    Log.logInfo("是否预警日:"+(_c_comparedDate.get(Calendar.YEAR) == _c_scheduleDate.get(Calendar.YEAR)
                           && _c_comparedDate.get(Calendar.MONTH) == _c_scheduleDate.get(Calendar.MONTH)
                           && _c_comparedDate.get(Calendar.DAY_OF_MONTH) == _c_scheduleDate.get(Calendar.DAY_OF_MONTH)), this.getClass().getName());

                    //如果当前日期已经到提醒日,则发送邮件
                    if (_c_comparedDate.get(Calendar.YEAR) == _c_scheduleDate.get(Calendar.YEAR)
                            && _c_comparedDate.get(Calendar.MONTH) == _c_scheduleDate.get(Calendar.MONTH)
                            && _c_comparedDate.get(Calendar.DAY_OF_MONTH) == _c_scheduleDate.get(Calendar.DAY_OF_MONTH)) {
                        sendMsaEmail(eqpId, msaEmail, scheduleDate,
                                periodName, periodDesc, accountDao,
                                mailManager);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        //4.备件寿命，按课别报警
        List sectionList = getEquipmentSection();
        if (sectionList.size() > 0) {
            for (Iterator it = sectionList.iterator(); it.hasNext();) {
                Map map = (Map) it.next();
                String section = (String) map.get("SECTION");
                String mailAddress = (String) map.get("MAIL_ADDRESS");
                sendSparePartEmail(section, mailAddress, mailManager);
            }
        }

        //5.菜单备份，提前10天报警一次，到期后天天报警
        List recipeBackupList = queryRecipeBackup();
        if (recipeBackupList.size() > 0) {
            for (Iterator it = recipeBackupList.iterator(); it.hasNext();) {
                Map map = (Map) it.next();
                sendRecipeBackupEmail(map, accountDao, mailManager);
            }
        }
	}

	//2014-5-4 备件寿命课别
	private List getEquipmentSection() {
		List list = null;
		String sql = "select * from equipment_section t where t.mail_address is not null and t.section not like '%工艺%'"
			+ " and t.dept_index in (select e.dept_index from equipment_dept e where e.equipment_dept in ('工程一部','工程二部'))"
			+ " order by t.section";

	    try {
	        list = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]",
	                this.getClass().getName());
	    }
	    return list;
	}

	//2014-5-4 备件寿命报警
	//2017-4-26 未使用登记报警 备件靶材：超过一周，耗件：超过一个月，石英件：超过三个月
	private void sendSparePartEmail(String section, String mailAddress, MailManager mailManager) {
		String body = "";
		String sendCc = null;
		boolean isExpireUse = false;//是否超过使用寿命
		boolean isExpireDoc = false;//是否超过7天未登记

		//1、通用寿命规则 mcs_material_info.usable_time_limit
		List list1 = null;
		String sql = "select t.using_object_id,t.mtr_num,t.mtr_desc,t.mtr_grp,t.update_time,"
	       + "to_char(t.update_time + m.usable_time_limit, 'yyyy-mm-dd') forcast_change_date,"
	       + "round(sysdate - t.update_time) use_period, m.usable_time_limit,"
	       + "m.usable_time_limit - round(sysdate - t.update_time) left_days,"
	       + "nvl(m.pre_alarm_days,28) pre_alarm_days, count(*) count_number"
	       + " from mcs_material_status t, mcs_material_info m, account a, equipment e"
	       + " where t.material_index = m.material_index"
	       + " and t.trans_by = a.account_no(+)"
	       + " and t.using_object_id = e.equipment_id"
	       + " and t.status = 'USING' and m.usable_time_limit > 0"
	       + " and t.update_time + m.usable_time_limit <= trunc(sysdate) + nvl(m.pre_alarm_days,28) + 1"
	       + " and (t.mtr_num, t.using_object_id) not in (select mtr_num, using_object_id from mcs_mtr_object where usable_time_limit>0)"
	       + " and e.section='" + section + "'"
	       + " group by t.using_object_id,t.mtr_num,t.mtr_desc,t.mtr_grp,t.update_time,m.usable_time_limit,m.pre_alarm_days"
	       + " order by t.using_object_id, t.mtr_num, t.update_time";

		try {
			list1 = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			e.printStackTrace();
	        Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]", this.getClass().getName());
		}

		if (list1.size() > 0) {
			body += "MCS备耗件寿命设定：\n";
            body = setBodyPartExpireUse(body, list1);
        }

		//2、特殊寿命规则 mcs_mtr_object.usable_time_limit
		List list2 = null;
		sql = "select t.using_object_id,t.mtr_num,t.mtr_desc,t.mtr_grp,t.update_time,"
		       + "to_char(t.update_time + m.usable_time_limit, 'yyyy-mm-dd') forcast_change_date,"
		       + "round(sysdate - t.update_time) use_period, m.usable_time_limit,"
		       + "m.usable_time_limit - round(sysdate - t.update_time) left_days,"
		       + "nvl(m1.pre_alarm_days,28) pre_alarm_days, count(*) count_number"
		       + " from mcs_material_status t, mcs_mtr_object m, account a, equipment e, mcs_material_info m1"
		       + " where t.mtr_num = m.mtr_num and t.using_object_id = m.using_object_id"
		       + " and t.trans_by = a.account_no(+)"
		       + " and t.using_object_id = e.equipment_id"
		       + " and t.material_index = m1.material_index"
		       + " and t.status = 'USING' and m.usable_time_limit > 0"
		       + " and t.update_time + m.usable_time_limit <= trunc(sysdate) + nvl(m1.pre_alarm_days,28) + 1"
		       + " and e.section='" + section + "'"
		       + " group by t.using_object_id,t.mtr_num,t.mtr_desc,t.mtr_grp,t.update_time,m.usable_time_limit,m1.pre_alarm_days"
		       + " order by t.using_object_id, t.mtr_num, t.update_time";

		try {
			list2 = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			e.printStackTrace();
	        Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]", this.getClass().getName());
		}

		if (list2.size() > 0) {
			body += "\nMCS特殊寿命设定：\n";
            body = setBodyPartExpireUse(body, list2);
        }

        if (body.indexOf("剩余-") > -1) {
			isExpireUse = true;
		}

		//3、超过7天未登记使用报警，不含200元内的备件
        //新领用
		List list3 = null;
		sql = "select t1.*, nvl(t2.pre_sto_number, 0) pre_sto_number,t2.mtr_desc,t2.mtr_grp, trunc(sysdate)-trunc(t1.doc_time) doc_days"
	       + " from (select t.mtr_num,t.dept_index,a.account_section,min(t.doc_time) doc_time,sum(t.qty - t.active_qty) left_no"
	       + " from mcs_material_sto_req t, account a"
	       + " where t.recipient = a.account_no and t.mtr_grp in ('20002P', '20002S','100015','100018') and t.active_flag = 'N' and t.qty > 0"
	       + " and t.mtr_num not in (select mtr_num from mcs_sap_mtr_table where mtr_grp = '20002P' and moving_average_price <= 200)"
	       + " group by t.mtr_num, t.dept_index, a.account_section) t1, mcs_material_info t2"
	       + " where t1.mtr_num = t2.mtr_num and t1.dept_index = t2.dept_index and t1.left_no > nvl(t2.pre_sto_number, 0)"
	       + " and t1.doc_time < trunc(sysdate) - 7 and t1.account_section='" + section + "'"
	       + " order by t1.doc_time";

		try {
			list3 = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			e.printStackTrace();
	        Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]", this.getClass().getName());
		}
		
		if (list3.size() > 0) {
			body = setBodyPartExpireDoc(body, list3, "领用");						
        }
		
		//暂存
		List list4 = null;		
		sql = "select t1.*, nvl(t2.pre_sto_number, 0) pre_sto_number,t2.mtr_desc,t2.mtr_grp, trunc(sysdate)-trunc(t1.doc_time) doc_days"
		       + " from (select t.mtr_num,t.dept_index,a.account_section,min(t.doc_time) doc_time,count(*) left_no"
		       + " from mcs_material_status t, account a"
		       + " where t.trans_by = a.account_no and t.mtr_grp in ('20002P', '20002S','100015','100018') and t.status like 'CABINET%'"
		       + " and t.mtr_num not in (select mtr_num from mcs_sap_mtr_table where mtr_grp = '20002P' and moving_average_price <= 200)"
		       + " group by t.mtr_num, t.dept_index, a.account_section) t1, mcs_material_info t2"
		       + " where t1.mtr_num = t2.mtr_num and t1.dept_index = t2.dept_index and t1.left_no > nvl(t2.pre_sto_number, 0)"
		       + " and t1.doc_time < trunc(sysdate) - 7 and t1.account_section='" + section + "'"
		       + " order by t1.doc_time";

		try {
			list4 = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);			
		} catch (SQLException e) {
			e.printStackTrace();
	        Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]", this.getClass().getName());
		}
		
		if (list4.size() > 0) {
			body = setBodyPartExpireDoc(body, list4, "暂存");
        }

        if (body.indexOf("未登记") > -1) {
        	isExpireDoc = true;
		}

		// send mail
        if (!"".equals(body)) {
            String subject = section + " - 备耗件寿命到期 & 未登记使用报警";
            if (isExpireUse) {
            	subject += " [寿命已到期]";
            }

            if (isExpireDoc) {
            	subject += " [超期未登记]";
            }

            //mailManager.sendMail("dinghh@csmc.crmicro.com", null, "[F5 PMS/MCS]\n" + body, subject);
            mailManager.sendMail(mailAddress, null, "[F5 PMS/MCS]\n" + body, subject);
            Log.logInfo(subject, this.getClass().getName());
            Log.logInfo("send mail success [" + section + "/" + mailAddress + "/" + sendCc + "]", this.getClass().getName());
            //Log.logInfo(body, this.getClass().getName());
        }
    }

	//超期未登记使用清单: 新领 或 暂存
	private String setBodyPartExpireDoc(String body, List list, String typeName) {
		boolean isAdd = false;
		
		for (Iterator it = list.iterator(); it.hasNext();) {
		    Map map = (Map) it.next();
		    String mtrNum = (String) map.get("MTR_NUM");
		    String mtrDesc = (String) map.get("MTR_DESC");
		    String mtrGrp = (String) map.get("MTR_GRP");
		    String docTime = (String) map.get("DOC_TIME");//(最小)领用时间
		    String leftNo = (String) map.get("LEFT_NO");
		    String preStoNumber = (String) map.get("PRE_STO_NUMBER");//提前领用量(安全领用量)
		    String docDays = (String) map.get("DOC_DAYS");//(最大)已领用天数

		  //备件/靶材：超过一周，耗件：超过一个月，石英件：超过三个月
		    int iDocDays = Integer.parseInt(docDays);
		    if ("20002P".equals(mtrGrp) && iDocDays > 7 || "100015".equals(mtrGrp) && iDocDays > 7 || "20002S".equals(mtrGrp) && iDocDays > 30 || "100018".equals(mtrGrp) && iDocDays > 90) {
		    	if (isAdd == false) {
		    		body += "\nMCS " + typeName + "超期未登记使用：\n";
		    		isAdd = true;
		    	}
		    	
		    	body += "料号[" + mtrNum + " / " + mtrDesc + " / " + mtrGrp + " / 可提前领用" + preStoNumber + "]，"
		    	+ "未登记数量[" + leftNo + "]，"
		    	+ "领用日期[" + docTime + "]，"
		    	+ "已领用[" + docDays + "天]。\n";
		    }
		}
		return body;
	}
	
	//备件 通用寿命到期 或 特殊寿命到期
	private String setBodyPartExpireUse(String body, List list) {
		for (Iterator it = list.iterator(); it.hasNext();) {
		    Map map = (Map) it.next();
		    String eqpId = (String) map.get("USING_OBJECT_ID");
		    String mtrNum = (String) map.get("MTR_NUM");
		    String mtrDesc = (String) map.get("MTR_DESC");
		    String mtrGrp = (String) map.get("MTR_GRP");
		    String updateTime = (String) map.get("UPDATE_TIME");
		    String forcastChangeDate = (String) map.get("FORCAST_CHANGE_DATE");
		    String usableTimeLimit = (String) map.get("USABLE_TIME_LIMIT");
		    String usePeriod = (String) map.get("USE_PERIOD");
		    String leftDays = (String) map.get("LEFT_DAYS");
		    String preAlarmDays = (String) map.get("PRE_ALARM_DAYS");
		    String countNumber = (String) map.get("COUNT_NUMBER");

		    int iLeftDays = Integer.parseInt(leftDays);
		    int iPreAlarmDays = Integer.parseInt(preAlarmDays);
		    if (iLeftDays <= iPreAlarmDays) {
		    	body += "设备[" + eqpId + "]，"
		    	+ "料号[" + mtrNum + " / " + mtrDesc + " / " + mtrGrp + "]，"
		    	+ "数量[" + countNumber + "]，"
		    	+ "使用日期[" + updateTime + "]，" + "预计更换日期[" + forcastChangeDate + "]，"
		    	+ "设定寿命[" + usableTimeLimit + "天 / 已使用" + usePeriod + "天 / 剩余" + leftDays + "天]。\n";
		    }
		}

		return body;
	}

	/**
     * @brief 查询到预设提醒天数的MSA设备,发Mail
     * @return
     */
    protected List queryMsaPmForAlert() {
        // 按 pm_next_starttime 判断
        String sql = "select * from ("
                + " select t1.equipment_id,"
                + " t1.msa_email,"
                + " t3.period_name,"
                + " t3.period_desc,"
                + " t2.schedule_date,"
                + " t3.default_days,"
                + " RANK() OVER (PARTITION BY  t1.equipment_id ORDER BY t2.schedule_date) adjust_flag  "// 根据equipment_id排等级,1,2,3,...
                + " from equipment t1,"
                + " equipment_schedule t2,"
                + " default_period t3"
                + " where t1.msa='Y'"
                + " and t3.period_name like 'MSA%'"
                + " and t2.equipment_id =t1.equipment_id and t2.schedule_date > sysdate and t2.event_index is null"
                + " and t3.period_index=t2.period_index and t3.enabled=1)"
                + " where adjust_flag = 1";// 取等级为1的值
        List list = null;

        try {
            list = SQLProcess.excuteSQLQuery(sql,
                    DataSourceFactory.PMS_DATASOURCE);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]",
                    this.getClass().getName());
        }
        return list;
    }

	/**
     * @param eqpId
     * @param msaEmail
     * @param scheduleDate
     * @param periodName
     * @param periodDesc
     *            Create on 2011-6-8 Update on 2011-6-8
     * @param accountDao
     * @param mailManager
     */
    private void sendMsaEmail(String eqpId, String msaEmail,
            String scheduleDate, String periodName, String periodDesc,
            AccountDao accountDao, MailManager mailManager) {
        // send mail
        String subject = "MSA设备[" + eqpId + "]PM提醒,PM周期为[" + periodName + "]";
        String body = "设备[" + eqpId + "]\n" + "PM周期[" + periodName + "]\n"
                + periodDesc + "\n" + "预定维护日期[" + scheduleDate + "]\n";

        //String sendTo = accountDao.getMailByGh(eqpId);
        //mailManager.sendMail(Constants.SEND_FROM, msaEmail, null, null, subject, body);
     /**   mailManager.sendMail(msaEmail, "XXXX", body.toString(), subject); */
        mailManager.sendMail(msaEmail, null, body.toString(), subject);
        Log.logInfo("sendMsaEmail success [" + eqpId + "/" + periodName + "/" + msaEmail + "]",
                this.getClass().getName());

        //mailManager.sendMail("zhaoyu@csmc.crmicro.com", null, body.toString(), subject);
        //mailManager.sendMail("cuiling@csmc.crmicro.com", null, body.toString(), subject);
    }

	/**
     * 今天将要到期的保养（明日将超时），发Mail提醒
     * @return
     * update at 2011-11-29
     * 增加了msa_email
     * @author qinchao
     */
    protected List queryDelayPmForAlarm() {
        // 按 pm_next_starttime 判断
        /*
         * String sql =
         * "select t1.period_index,t4.period_name,t1.equipment_id,t1.next_pm_date,"
         * +
         * "t2.section,t2.equipment_engineer,t3.section_leader,t4.eqp_status,t4.is_update_promis"
         * +
         * " from pm_next_starttime t1,equipment t2,equipment_section t3,default_period t4"
         * + " where t1.equipment_id=t2.equipment_id" +
         * " and t2.section=t3.section" + " and t1.period_index=t4.period_index"
         * + " and (t1.equipment_id,t1.next_pm_date) not in " +
         * "(select equipment_id,trunc(last_pm_date)" +
         * " from pm_next_starttime" +
         * " where trunc(last_pm_date)=trunc(sysdate))" +
         * " and t1.next_pm_date = trunc(sysdate)" + " and t4.enabled=1";
         */

        // 按 equipment_schedule 判断
        // 日保养不判断，t4.default_days != 1
        /*
         * String sql = "select t1.period_index,t4.period_name,t1.equipment_id,"
         * +
         * "t2.section,t2.equipment_engineer,t3.section_leader,t4.eqp_status,t4.is_update_promis"
         * +
         * " from equipment_schedule t1,equipment t2,equipment_section t3,default_period t4"
         * + " where t1.equipment_id=t2.equipment_id" +
         * " and t2.section=t3.section" + " and t1.period_index=t4.period_index"
         * + " and t1.schedule_date+nvl(t4.warning_days,0) = trunc(sysdate)" +
         * " and t1.event_index is null and t1.schedule_date<sysdate" +
         * " and t4.default_days != 1" + " and t4.enabled=1";
         */

        String sql = "select period_index,period_name,equipment_id,next_pm_date,"
                + "section,equipment_engineer,section_leader,eqp_status,is_update_promis,msa_email"
                + " from pm_alarm_view"
                + " where next_pm_date = trunc(sysdate)";

        List list = null;
        try {
            list = SQLProcess.excuteSQLQuery(sql,
                    DataSourceFactory.PMS_DATASOURCE);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]",
                    this.getClass().getName());
        }
        return list;
    }

	/**
	 * 查询PM未作超时记录
	 *
	 * @return
	 * @throws SQLException
	 */
	private List queryDelayPM() {
		String sql = "select t1.period_index,t4.period_name,t1.equipment_id,t1.next_pm_date,"
				+ "t2.section,t2.equipment_engineer,t3.section_leader,t4.eqp_status,t4.is_update_promis"
				+ " from pm_next_starttime t1,equipment t2,equipment_section t3,default_period t4"
				+ " where t1.equipment_id=t2.equipment_id"
				+ " and t2.section=t3.section"
				+ " and t1.period_index=t4.period_index"
				+ " and t1.next_pm_date<=sysdate-1" + " and t4.enabled=1";
		List list = null;

		try {
			list = SQLProcess.excuteSQLQuery(sql,
					DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			e.printStackTrace();
			Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]",
					this.getClass().getName());
		}
		return list;
	}

	protected void checkUndoTodayPm(MailManager mailManager,
			AccountDao accountDao) {
		Log.logInfo("--start check undo pm today", this.getClass().getName());
		List list = queryUndoTodayPm();
		if (list != null && list.size() > 0) {
			int index = 0;
			//String subject = "今日将作未作保养提醒";

			String body = "";
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				int total = Integer.parseInt(String.valueOf(map.get("TOTAL")));
				String eqpId = (String) map.get("EQUIPMENT_ID");
				String periodName = (String) map.get("PERIOD_NAME");
				String eqpEnger = (String) map.get("EQUIPMENT_ENGINEER");
				String sectionLeader = (String) map.get("SECTION_LEADER");

				body += "设备[" + eqpId + "] \n" + "PM周期[" + periodName + "]\n";

				String subject = "今日设备保养提醒 - 设备[" + eqpId + "],PM周期[" + periodName + "]：今日需做保养表单";
				Log.logInfo("|--info:[" + eqpId + "/" + periodName + "]", this.getClass().getName());
				index++;

				if (index == total) {
					String sendTo = accountDao.getMailByGh(eqpEnger);
					String sendCc = accountDao.getMailByGh(sectionLeader);

					mailManager.sendMail(sendTo, sendCc, body.toString(), subject);
					Log.logInfo("|--sendMail:[" + sendTo + "/" + sendCc + "]", this.getClass().getName());
					body = "";
					index = 0;
				}
			}
		}
	}

	private List queryUndoTodayPm() {
		String sql = "select a.*, b.total" + "  from undo_today_pm a,"
				+ "       (select count(*) total, equipment_engineer"
				+ "          from undo_today_pm"
				+ "         group by equipment_engineer) b"
				+ " where a.equipment_engineer = b.equipment_engineer"
				+ " order by a.equipment_engineer,a.equipment_id";

		List list = null;
		try {
			list = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			e.printStackTrace();
			Log.logError("|--sql:[" + sql + "] error [" + e.getMessage() + "]",
					this.getClass().getName());
		}
		return list;
	}

	/**
     * @brief 查询等于10天或已到期的菜单备份保养计划
     * @return List
     */
    protected List queryRecipeBackup() {
        String sql = "select t1.equipment_id,t1.schedule_date, t1.schedule_date-trunc(sysdate) left_days,"
                + " t2.recipe_backup_email,t3.period_name,t4.dept_leader"
                + " from equipment_schedule t1, equipment t2, default_period t3, equipment_dept t4"
                + " where t1.equipment_id=t2.equipment_id and t1.period_index=t3.period_index and t2.dept_index=t4.dept_index"
                + " and t1.event_index is null and t2.recipe_backup_email is not null and t3.period_name like '菜单备份%'"
                + " and (t1.schedule_date-trunc(sysdate)=10 or t1.schedule_date-trunc(sysdate)<=0)";
        List list = null;

        try {
            list = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]",
                    this.getClass().getName());
        }
        return list;
    }

	/**
     * @param map
     * @param accountDao
     * @param mailManager
     */
    private void sendRecipeBackupEmail(Map map, AccountDao accountDao, MailManager mailManager) {
        // send mail
    	String eqpId = (String) map.get("EQUIPMENT_ID");
        String recipeBackupEmail = (String) map.get("RECIPE_BACKUP_EMAIL");
        String scheduleDate = (String) map.get("SCHEDULE_DATE");
        String periodName = (String) map.get("PERIOD_NAME");
        String leftDays = (String) map.get("LEFT_DAYS");
        String deptLeader = (String) map.get("DEPT_LEADER");

        String sendCc = null;
        String subject = "菜单备份 - 设备[" + eqpId + "],PM周期[" + periodName + "]：";
        String body = "设备[" + eqpId + "]\n" + "PM周期[" + periodName + "]\n" + "预定维护日期[" + scheduleDate + "]\n";

        int iLeftDays = Integer.parseInt(leftDays);
        if (iLeftDays == 10) {
        	subject = subject + "剩余10天到期";
        } else if (iLeftDays == 0) {
        	subject = subject + "今天已到期";
        } else if (iLeftDays < 0) {
        	//抄送部门经理和厂长郭佳惠
        	subject = subject + "超期未做";
            sendCc = accountDao.getMailByGh(deptLeader) + accountDao.getMailByGh("11066703");
        }

        //mailManager.sendMail("dinghh@csmc.crmicro.com", null, body, subject);
        mailManager.sendMail(recipeBackupEmail, sendCc, body, subject);
        Log.logInfo("sendRecipeBackupEmail success [" + eqpId + "/" + periodName + "/" + recipeBackupEmail + "]",
                this.getClass().getName());
    }

	protected void checkRequisitionOverdue(MailManager mailManager) {
		List list = queryRequisitionOverdue();
		if (list != null && list.size() > 0) {
			String sendTo = (String) ((Map) list.get(0)).get("EMAIL");
			StringBuffer sb = new StringBuffer();
			String subject = "你领用的化学品已一个月未上设备使用，请及时使用！";
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				String email = (String) map.get("EMAIL");
				if (!sendTo.equals(email) && sb.length() > 0) {
					mailManager.sendMail(sendTo, "caolianying1@rxgz.crmicro.com", sb.toString(), subject);
					Log.logInfo("checkRequisitionOverdue send mail success[" + sendTo + "/" + subject + "]", this.getClass().getName());
					sendTo = email;
				}
				sb.append("物料号：").append((String) map.get("MTR_NUM")).append(" ")
					.append("物料描述：").append((String) map.get("MTR_DESC")).append(" ")
					.append("领用日期：").append((String) map.get("DOC_TIME")).append(" ")
					.append("领用数量：").append((String) map.get("QTY")).append("\n");
				if (i == list.size() - 1) {
					mailManager.sendMail(sendTo, "caolianying1@rxgz.crmicro.com", sb.toString(), subject);
					Log.logInfo("checkRequisitionOverdue send mail success[" + sendTo + "/" + subject + "]", this.getClass().getName());
				}
			}
		}
	}

	/**
	 * 查询超一个月未使用的化学品领用记录
	 *
	 * @return
	 * @throws SQLException
	 */
	private List queryRequisitionOverdue() {
		StringBuffer sb = new StringBuffer();
		sb.append(" select 'xiongmoyong@rxgz.crmicro.com' EMAIL, t.mtr_num, t.mtr_desc, t.doc_time, sum(t.qty) qty ");
		sb.append(" from mcs_material_sto_req t, dept_leader t1 ");
		sb.append(" where t.doc_time < add_months(sysdate, -1) ");
		sb.append(" and t.active_flag = 'N' ");
		sb.append(" and t.qty > 0 ");
		sb.append(" and t.dept_index = '10002' ");
		sb.append(" and t.mtr_grp = '100013' ");
		sb.append(" and t.cost_center like '%' || t1.dept_desc || '%' ");
		sb.append(" and t1.leader = t2.GH ");
		sb.append(" group by t.mtr_num, t.mtr_desc, t.doc_time ");
		sb.append(" order by  t.doc_time ");
		String sql = sb.toString();
		List list = null;
		try {
			list = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			Log.logError("sql:[" + sql + "] error [" + e.getMessage() + "]", this.getClass().getName());
		}
		return list;
	}

	/**
	 * 关键备件每天10点，关键备件寿命预警、超期邮件提醒
	 * 
	 * @param mimeMailManager
	 * @param accountDao
	 */
	protected void checkKeyPartsUse(MimeMailManager mimeMailManager, AccountDao accountDao) {
		Log.logInfo("--start check key Parts Use -----", this.getClass().getName());
		List sectionList = this.querySections();
		if (sectionList != null && sectionList.size() > 0) {
			for (int k = 0; k < sectionList.size(); k++) {

				Map sectionMap = (Map) sectionList.get(k);
				Map valueMaps = new HashMap();
				String section = (String) sectionMap.get("SECTION");
				String deptIndex = (String) sectionMap.get("DEPT_INDEX");
				// String[] leaderMails=accountDao.getDeptLeaderMailsByDeptIndex(deptIndex);
				String[] mails = accountDao.getSectionMailBySection(section);
				Map WarnAndErrorMap = queryKeyPartsUseWarnAndErrorMap(section);
				List keyPartsWarnList = (List) WarnAndErrorMap.get("warnList");
				List keyPartsErrorList = (List) WarnAndErrorMap.get("errorList");
				if (mails != null && mails.length > 0) {
					int lmLength = 0;
					// if(leaderMails!=null&&mails.length>0){
					// lmLength=leaderMails.length;
					// }
					String[] sendTo = new String[mails.length + 1];
					System.arraycopy(mails, 0, sendTo, 0, mails.length);
					// if(leaderMails!=null&&mails.length>0){
					// System.arraycopy(leaderMails, 0, sendTo,mails.length, lmLength);
					// }
/**					sendTo[sendTo.length - 1] = "XXXX"; */

					if ((keyPartsWarnList != null && keyPartsWarnList.size() > 0)
							|| (keyPartsErrorList != null && keyPartsErrorList.size() > 0)) {
						valueMaps.put("keyPartsWarnList", keyPartsWarnList);
						valueMaps.put("keyPartsErrorList", keyPartsErrorList);
					}
					if (valueMaps != null && valueMaps.size() > 0) {
						Log.logInfo("进入keyPart--------------\n"+valueMaps+"\n user"+sendTo, module);
						mimeMailManager.sendMail(sendTo, "PMS系统-" + section + "-关键备件寿命提醒！", "keyPartsUseCheckMail.ftl",
								valueMaps);
					}

				}
			}
		}
	}

	protected List querySections() {
		List sectionlist = null;
		String sql = "select distinct t.section,t.dept_index from equipment_section t";
		try {
			sectionlist = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
		} catch (SQLException e) {
			Log.logError(e.getMessage(), module);
		}
		return sectionlist;
	}

	private Map queryKeyPartsUseWarnAndErrorMap(String section) {
		String sql = " SELECT T1.key_parts_id,t1.eqp_id,t2.keydesc,  t2.limit_type,t2.parts_id,t2.parts_name,eqp_type,error_spec, "
				+ " warn_spec,notify,t3.section,t3.mail_address, "
				+ "  nvl(floor(sysdate - t1.update_time),0) + NVL(T1.INIT_LIFE,0)   actul ,NVL(t4.delay_life, 0)delay_life,t2.mustchange,to_char(t1.update_time,'yyyy/MM/dd HH24:MI:SS')update_time,t1.init_life "
				+ " FROM KEY_PARTS_USE T1 " + " left join KEY_EQP_PARTS T2 on T1.KEY_PARTS_ID = T2.KEY_PARTS_ID "
				+ " left join equipment_section t3 on t2.notify = t3.section_index "
				+ " left join (select sum(t.delay_life) delay_life, t.key_use_id from key_parts_delay_info t group by t.key_use_id) t4 on t1.key_use_id = t4.key_use_id "
				+ " WHERE t1.STATUS = 'USING' "
				// + " AND T2.LIMIT_TYPE = 'TIME(天)' "
				+ " and t2.is_alarm = 'Y' " + " AND T2.ENABLE ='Y' " // is not null "
				// + " AND (nvl(floor(sysdate - t1.update_time), 0) + NVL(T1.INIT_LIFE, 0)
				// -NVL(t4.delay_life, 0)) > t2.warn_spec "
				// + " AND (nvl(floor(sysdate - t1.update_time), 0) + NVL(T1.INIT_LIFE, 0)
				// -NVL(t4.delay_life, 0)) <= t2.error_spec "
				+ " AND t3.section='" + section + "' " + " order by t1.eqp_id ";

		List list = null;
		try {
			Map map = new HashMap();
			List warnList = new ArrayList();
			List errorList = new ArrayList();
			list = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map partsUseKeyMap = (Map) list.get(i);
					String keyPartsId = (String) partsUseKeyMap.get("KEY_PARTS_ID");
					String limitType = (String) partsUseKeyMap.get("LIMIT_TYPE");
					String warnSpec = (String) partsUseKeyMap.get("WARN_SPEC");
					String errorSpec = (String) partsUseKeyMap.get("ERROR_SPEC");
					String initLife = (String) partsUseKeyMap.get("INIT_LIFE");
					String delayLife = (String) partsUseKeyMap.get("DELAY_LIFE");
					String actul = (String) partsUseKeyMap.get("ACTUL");
					String eqpId = (String) partsUseKeyMap.get("EQP_ID");
					String updateTime = (String) partsUseKeyMap.get("UPDATE_TIME");

					if (limitType != null && !limitType.equals("TIME(天)")) {
						Log.logInfo("进入keyPart-------------limitType-\n"+limitType+"\n user", module);
						// actul=getActulFromFdc(eqpId, limitType, updateTime,null);
						// if(actul.equals("fdcError") || actul.equals("relationError")){
						// actul="0";//对应eqpid，limittype没有fdc收值时，使用寿命置为零
						// }else{
						// actul=(Double.parseDouble(actul)+Double.parseDouble(initLife)+"").trim();
						// }
						actul = (Double.parseDouble(actul) + Double.parseDouble(initLife) + "").trim();
						partsUseKeyMap.remove("ACTUL");
						partsUseKeyMap.put("ACTUL", actul);
					}
					double warnLife = Long.parseLong(warnSpec);
					double errorLife = Long.parseLong(errorSpec);
					double actulLife = Double.parseDouble(actul);
					if (limitType != null && !limitType.equals("")) {
//						Log.logInfo("进入keyPart-------------limitType-\n"+limitType+"\n user", module);
//						Log.logInfo("进入keyPart-------------warnLife-\n"+warnLife+"\n user", module);
//						Log.logInfo("进入keyPart-------------errorLife-\n"+errorLife+"\n user", module);
//						Log.logInfo("进入keyPart-------------actulLife-\n"+actulLife+"\n user", module);
						warnLife = warnLife + Long.parseLong(delayLife);
						errorLife = errorLife + Long.parseLong(delayLife);
						;
						if (actulLife > warnLife && actulLife <= errorLife) {
							warnList.add(partsUseKeyMap);
						} else if (actulLife > errorLife) {
							errorList.add(partsUseKeyMap);
						}
					}
				}
			}
			map.put("warnList", warnList);
			map.put("errorList", errorList);

			return map;

		} catch (SQLException e) {
			e.printStackTrace();
			Log.logError("|--sql:[" + sql + "] error [" + e.getMessage() + "]", this.getClass().getName());
		}
		return null;
	}

	/**
	 * 获取waferCount，RFHours等FDC寿命统计
	 * 
	 * @throws SQLProcessException
	 */
	public static String getActulFromFdc(String eqp_id, String lifeType, String createTime, String unuseTime) {
		String actul = "";
		String sqlcontion = "";
		String sqlcontion1 = "";
		String sql0 = "select count(*) count from eqp_fdc_relation_hist t where t.equipment_id='" + eqp_id
				+ "' and t.life_type='" + lifeType + "' and t.update_time>=to_date('" + createTime
				+ "','YYYY/MM/DD HH24:MI:SS') ";
		String sql1 = "select * from eqp_fdc_relation t where t.equipment_id='" + eqp_id + "' and t.life_type='"
				+ lifeType + "' and t.enable='Y' ";
		String sql = "select nvl((max(t.value_amount)-min(t.value_amount)),-1)actul from fdc_paravalue_sync t "
				+ "where t.eqp_id='" + eqp_id + "' and t.life_type='" + lifeType + "' and t.datetime>=to_date('"
				+ createTime + "','YYYY/MM/DD HH24:MI:SS') ";
		if (unuseTime != null) {
			sqlcontion = " and t.datetime<=to_date('" + unuseTime + "','YYYY/MM/DD HH24:MI:SS') ";
			sqlcontion1 = " and t.update_time<=to_date('" + unuseTime + "','YYYY/MM/DD HH24:MI:SS') ";
		} else {
			sqlcontion = " and t.datetime<=sysdate ";
			sqlcontion1 = " and t.update_time<=sysdate ";
		}
		sql = sql + sqlcontion;
		sql0 = sql0 + sqlcontion1;
		List list0;
		try {
			list0 = SQLProcess.excuteSQLQuery(sql0, DataSourceFactory.PMS_DATASOURCE);
			String count = (String) ((Map) list0.get(0)).get("COUNT");
			List list1 = SQLProcess.excuteSQLQuery(sql1, DataSourceFactory.PMS_DATASOURCE);
			if (list1 == null || list1.size() <= 0 || !count.equals("0")) {
				actul = "relationError";
			} else {
				String enable = (String) ((Map) list1.get(0)).get("ENABLE");
				List list = SQLProcess.excuteSQLQuery(sql, DataSourceFactory.PMS_DATASOURCE);
				for (int i = 0; i < list.size(); i++) {
					Map actualMap = (Map) list.get(i);
					actul = (String) actualMap.get("ACTUL");
					if (actul.equals("-1")) {// 备件上机期间fdc没有收值，则实际寿命设为0
						// actul="fdcError";
						actul = "0";
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return actul.trim();
	}
}
