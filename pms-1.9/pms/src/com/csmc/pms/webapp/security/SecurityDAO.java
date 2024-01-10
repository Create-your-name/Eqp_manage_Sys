/*
 * ��Ȩ���ϓP������Ϻ������޹�˾����
 * ���������ϓP�����˽�л�Ҫ����
 * δ������˾��Ȩ�����÷Ƿ������͵���
 * ���ڱ���˾��Ȩ��Χ�ڣ�ʹ�ñ�����
 * ��������Ȩ��
 */

package com.csmc.pms.webapp.security;

import java.util.*;
 
import javax.swing.tree.DefaultMutableTreeNode;

import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.security.model.*;
 
/**
    *�� SecurityDAO.java 
    *ֱ�������ݿ���в���������Menu���ṹ
    *@version  1.0  2004-8-10
    *@author   TONY
    */
public class SecurityDAO {
	public static String module = SecurityDAO.class.getName();
	
	//�����е�Ȩ�����һ�����ṹ
	public static DefaultMutableTreeNode getAllPrivTree(GenericDelegator delegator) {
		//��ʱpmsDelegator
		GenericDelegator pmDelegator = delegator;
        List result = null;
        //ʹ��SQL������һ�����ṹ
		String SQL = "SELECT LEVEL, l.MENU_ID, l.MENU_DESC, l.PARENT_MENU_ID, " +
						"l.MENU_TYPE, l.FUNCTION_TYPE, l.ACTION_TAG " + 
						"FROM MENU_HIERARCHY l " + 
						"START WITH l.parent_menu_id IS NULL " +  
						"CONNECT BY PRIOR l.menu_id = l.parent_menu_id ORDER BY MENU_ID";
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new MenuModel());
		DefaultMutableTreeNode tempNode = null;
		int level = 0;
		
		try {
			//ִ��SQL
            result = SQLProcess.excuteSQLQuery(SQL, pmDelegator);
            tempNode = root;
            
            for (int i = 0; i < result.size(); i++) {
            	//��Menu��Ϣ������MenuModel��
            	HashMap map = (HashMap)result.get(i);
            	String sLevle = (String)map.get("LEVEL");
               	int rLevel = Integer.parseInt(sLevle);
               	MenuModel model = new MenuModel();
               	model.setMenuId((String)map.get("MENU_ID"));
               	model.setMenuDesc((String)map.get("MENU_DESC"));
               	model.setMenuType((String)map.get("MENU_TYPE"));
               	model.setParentMenuId((String)map.get("PARENT_MENU_ID"));
               	model.setFunctionType((String)map.get("FUNCTION_TYPE"));
               	model.setActoinTag((String)map.get("ACTION_TAG"));
               	               	
            	if (level == 0) {
            		//���Ƚ���һ���ڵ��������
            		tempNode.add(new DefaultMutableTreeNode(model));
            		level = rLevel;
            	} else {
            		if (level < rLevel) {
            			//����ϴ�levelС�ڵ�ǰlevel����������ӽڵ���
            			tempNode = (DefaultMutableTreeNode)tempNode.getLastChild();
            			tempNode.add(new DefaultMutableTreeNode(model));
            			level = rLevel;
            		} else if (level > rLevel){
            			//����ϴ�level���ڵ�ǰlevel�������levle���ϲ��ң�������ڵ�
            			int j = level - rLevel;
            			while (j > 0) {
            				tempNode = (DefaultMutableTreeNode)tempNode.getParent();
            				j--;
            			}
            			tempNode.add(new DefaultMutableTreeNode(model));
            			level = rLevel;
            		} else {
            			//����ϴ�level���ڵ�ǰlevel����ֱ�Ӳ���
            			tempNode.add(new DefaultMutableTreeNode(model));
            		}
            	}
            	
            }        
        }  catch (Exception ex) {
            Debug.logError(ex,module);
        } finally {
            
        }
        return (DefaultMutableTreeNode)tempNode.getRoot();
	}
	
	//���ݵõ���ȫ���Ĳ˵������û�Ȩ�ޣ��õ��û��Ĳ˵���
	public static DefaultMutableTreeNode getUserPrivTree(GenericDelegator delegator, List guiPrivMap) {
		
		DefaultMutableTreeNode root = getAllPrivTree(delegator);
		
		//Ĭ�Ͻ�����Ȩ����Ϊfalse
		Enumeration enumeration = root.preorderEnumeration();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enumeration.nextElement();
			MenuModel model = null;
			model = (MenuModel)cNode.getUserObject();
			if ("FUNCTION".equalsIgnoreCase(model.getMenuType())) {
				if (guiPrivMap.contains(model.getMenuDesc())) {
					model.setHasPriv(true);
				}
			} else {
				model.setHasPriv(false);
			}
		}
		
		//�������Ĳ���������ÿ���Ȩ��
		for (int i = 0; i <= root.getDepth() - 1; i++) {
			enumeration = root.preorderEnumeration();
			while (enumeration.hasMoreElements()) {
				DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enumeration.nextElement();
				MenuModel model = (MenuModel)cNode.getUserObject();
				Enumeration children = cNode.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
					MenuModel childModel = (MenuModel)childNode.getUserObject(); 
					if (childModel.getHasPriv()) {
						model.setHasPriv(true);
					}
				}
			}
		}
		
	    return root;
	}
	/*
	//���ݵõ���ȫ���Ĳ˵������û�Ȩ�ޣ��õ��û��Ĳ˵���
	public static DefaultMutableTreeNode getUserPrivTreeAbs(GenericDelegator delegator, List guiPrivMap) {
		DefaultMutableTreeNode root = getAllPrivTree(delegator);
		
		//Ĭ�Ͻ�����Ȩ����Ϊfalse
		Enumeration enum = root.preorderEnumeration();
		while (enum.hasMoreElements()) {
			DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enum.nextElement();
			MenuModel model = null;
			model = (MenuModel)cNode.getUserObject();
			if ("FUNCTION".equalsIgnoreCase(model.getMenuType())) {
				if (guiPrivMap.contains(model.getMenuDesc())) {
					model.setHasPriv(true);
				}
			} else {
				model.setHasPriv(false);
			}
		}
		
		//�������Ĳ���������ÿ���Ȩ��
		for (int i = 0; i < root.getDepth() - 1; i++) {
			enum = root.preorderEnumeration();
			while (enum.hasMoreElements()) {
				DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enum.nextElement();
				MenuModel model = (MenuModel)cNode.getUserObject();
				Enumeration children = cNode.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
					MenuModel childModel = (MenuModel)childNode.getUserObject(); 
					if (childModel.getHasPriv()) {
						model.setHasPriv(true);
					}
				}
			}
		}
		
		return root;
	}*/
	
	//�����û��Ĳ˵������õ���ʾ�ַ���
	public static String generateUserMenu(DefaultMutableTreeNode root) {
		
		String menuList = "";
		
		MenuModel rootModel = (MenuModel)root.getUserObject();
		if (!rootModel.getHasPriv()) {
			return menuList;
		}
		
		Enumeration enumeration = root.preorderEnumeration();
		
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enumeration.nextElement();
			MenuModel model = (MenuModel)cNode.getUserObject();
			if (!model.getHasPriv()) {
				cNode.removeFromParent();
				enumeration = root.preorderEnumeration();
			}
		}
		
		enumeration = root.preorderEnumeration();
		//��ȡ���ڵ�
		enumeration.nextElement();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enumeration.nextElement();
			MenuModel model = null;
			model = (MenuModel)cNode.getUserObject();
			//����ʾFUNCTION�ڵ�
			if (!"FUNCTION".equalsIgnoreCase(model.getMenuType())) {
				//����ʾû��Ȩ�޵Ľڵ�
				if (model.getHasPriv()) {
					Enumeration children = cNode.children();
					boolean childFlag = false;
					while (children.hasMoreElements()) {
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
						MenuModel childModel = (MenuModel)childNode.getUserObject();
						//����ýڵ���ӽڵ���Ȩ�ޣ�����ΪMENU�ڵ㣬���ո��ڵ��ʽ��ʾ
						if (childModel.getHasPriv() && "MENU".equalsIgnoreCase(childModel.getMenuType())) {
							menuList = menuList + "0,";
							menuList = menuList + "\"" + model.getMenuDesc() + "\"," + "\"" + model.getMenuDesc() + "\",";
							menuList = menuList + "\"\"," + "\"\"," + "\"\"," + "3," + "\"\",";
							if (model.getActoinTag() != null) {
								menuList = menuList + "\"" + model.getActoinTag() + "\"," + "\"mainFrame\",";
							} else {
								menuList = menuList + "\"\"," + "\"mainFrame\",";
							}
							menuList = menuList + "-1,";
							childFlag = true;
							break;
						}
					}
					//����Ҷ�ӽڵ��ʽ������ʾ
					if (!childFlag) {
						menuList = menuList + "2,";
						menuList = menuList + "\"" + model.getMenuDesc() + "\"," + "\"" + model.getMenuDesc() + "\",";
						menuList = menuList + "\"\"," + "\"\"," + "3," + "\"\",";
						if (model.getActoinTag() != null) {
							menuList = menuList + "\"" + model.getActoinTag() + "\"," + "\"mainFrame\",";
						} else {
							menuList = menuList + "\"\"," + "\"mainFrame\",";
						}
						
						DefaultMutableTreeNode siblNode = cNode;
						boolean siblFlag = false;
						while (siblNode.getNextSibling() != null) {
							MenuModel siblModel = (MenuModel)siblNode.getUserObject();
							if (siblModel.getHasPriv()) {
								siblFlag = true;
								break;
							}
							siblNode = siblNode.getNextSibling();
						} 
						
						if (siblFlag) {
						} else {
							DefaultMutableTreeNode tempNode = cNode;
							//����ýڵ���¸��ڵ�Ĳ�������menuList�ϼ��϶�Ӧ�������1
							while (tempNode.getParent() != null) {
																
								siblFlag = false;
								while (tempNode.getNextSibling() != null) {
									MenuModel siblModel = (MenuModel)tempNode.getUserObject();
									if (siblModel.getHasPriv()) {
										siblFlag = true;
										break;
									}
									tempNode = tempNode.getNextSibling();
								} 
								if (siblFlag) {
									break;
								} else {
									tempNode = (DefaultMutableTreeNode)tempNode.getParent();
								}
								menuList = menuList + "1,";
							}
						}

					}
				}
			}
		}
		return menuList;
	}
}