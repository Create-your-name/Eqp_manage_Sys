/*
 * 版权归上揚软件（上海）有限公司所有
 * 本程序属上揚软件的私有机要资料
 * 未经本公司授权，不得非法传播和盗用
 * 可在本公司授权范围内，使用本程序
 * 保留所有权利
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
    *类 SecurityDAO.java 
    *直接与数据库进行操作，生成Menu树结构
    *@version  1.0  2004-8-10
    *@author   TONY
    */
public class SecurityDAO {
	public static String module = SecurityDAO.class.getName();
	
	//将所有的权限组成一个树结构
	public static DefaultMutableTreeNode getAllPrivTree(GenericDelegator delegator) {
		//临时pmsDelegator
		GenericDelegator pmDelegator = delegator;
        List result = null;
        //使用SQL，构建一个树结构
		String SQL = "SELECT LEVEL, l.MENU_ID, l.MENU_DESC, l.PARENT_MENU_ID, " +
						"l.MENU_TYPE, l.FUNCTION_TYPE, l.ACTION_TAG " + 
						"FROM MENU_HIERARCHY l " + 
						"START WITH l.parent_menu_id IS NULL " +  
						"CONNECT BY PRIOR l.menu_id = l.parent_menu_id ORDER BY MENU_ID";
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new MenuModel());
		DefaultMutableTreeNode tempNode = null;
		int level = 0;
		
		try {
			//执行SQL
            result = SQLProcess.excuteSQLQuery(SQL, pmDelegator);
            tempNode = root;
            
            for (int i = 0; i < result.size(); i++) {
            	//将Menu信息，存入MenuModel中
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
            		//首先将第一个节点存入树中
            		tempNode.add(new DefaultMutableTreeNode(model));
            		level = rLevel;
            	} else {
            		if (level < rLevel) {
            			//如果上次level小于当前level，则将其插入子节点中
            			tempNode = (DefaultMutableTreeNode)tempNode.getLastChild();
            			tempNode.add(new DefaultMutableTreeNode(model));
            			level = rLevel;
            		} else if (level > rLevel){
            			//如果上次level大于当前level，则根据levle向上查找，并插入节点
            			int j = level - rLevel;
            			while (j > 0) {
            				tempNode = (DefaultMutableTreeNode)tempNode.getParent();
            				j--;
            			}
            			tempNode.add(new DefaultMutableTreeNode(model));
            			level = rLevel;
            		} else {
            			//如果上次level等于当前level，则直接插入
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
	
	//根据得到的全部的菜单树和用户权限，得到用户的菜单树
	public static DefaultMutableTreeNode getUserPrivTree(GenericDelegator delegator, List guiPrivMap) {
		
		DefaultMutableTreeNode root = getAllPrivTree(delegator);
		
		//默认将所有权限设为false
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
		
		//根据树的层数来设置每层的权限
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
	//根据得到的全部的菜单树和用户权限，得到用户的菜单树
	public static DefaultMutableTreeNode getUserPrivTreeAbs(GenericDelegator delegator, List guiPrivMap) {
		DefaultMutableTreeNode root = getAllPrivTree(delegator);
		
		//默认将所有权限设为false
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
		
		//根据树的层数来设置每层的权限
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
	
	//根据用户的菜单树，得到显示字符串
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
		//不取根节点
		enumeration.nextElement();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)enumeration.nextElement();
			MenuModel model = null;
			model = (MenuModel)cNode.getUserObject();
			//不显示FUNCTION节点
			if (!"FUNCTION".equalsIgnoreCase(model.getMenuType())) {
				//不显示没有权限的节点
				if (model.getHasPriv()) {
					Enumeration children = cNode.children();
					boolean childFlag = false;
					while (children.hasMoreElements()) {
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
						MenuModel childModel = (MenuModel)childNode.getUserObject();
						//如果该节点的子节点有权限，并且为MENU节点，则按照父节点格式显示
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
					//按照叶子节点格式进行显示
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
							//如果该节点的下个节点的层数，在menuList上加上对应层数差的1
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