--query complete pmform
select t.PM_INDEX,
       t.EQUIPMENT_ID,
       t.PM_NAME,
       t3.ACCOUNT_NAME,
       t4.period_name,
       t.START_TIME,
       decode(t.form_type, 'NORMAL', '正常', 'PATCH', '补填') form_type,
       t.UPDATE_TIME,
       '完成' STATUS,
       t.JOB_TEXT,
       t.END_USER,
       t.END_TIME,
       ROUND((t.END_TIME - t.START_TIME) * 24, 2) man_hour
  from PM_FORM t, equipment t2, account t3, default_period t4
 where 1 = 1
   and t2.equipment_id = t.equipment_id
   and t2.maint_dept = '光刻部'
   and t3.ACCOUNT_NO = t.CREATE_USER
   and t.period_index = t4.period_index
   and t.equipment_id = 'ALCD01'
   and t.CREATE_TIME >= to_date('2008-06-01', 'yyyy-mm-dd hh24:mi:ss')
   and t.CREATE_TIME <= to_date('2008-07-06', 'yyyy-mm-dd hh24:mi:ss')
   and t.STATUS = '1'
 order by t.CREATE_TIME desc