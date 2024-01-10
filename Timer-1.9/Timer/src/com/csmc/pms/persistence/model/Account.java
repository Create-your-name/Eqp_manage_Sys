package com.csmc.pms.persistence.model;


/**
 * PMS Account 2007-9-11
 * @author dinghh
 *
 */
public class Account {
	private String accountNo;
    
    private String accountName;   
    
    private String mailAddress;
    
    private String accountDept;
    
    private String accountSection;
    
    private String accountPos;
    
    
    /**
     * @return ���� accountNo��
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo Ҫ���õ� accountNo��
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    /**
     * @return ���� accountName��
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @param accountName Ҫ���õ� accountName��
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }    
   
    /**
     * @return ���� mailAddress��
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * @param mailAddress Ҫ���õ� mailAddress��
     */
    public void setMailAddress(String mailAddress) {
    	if(mailAddress == null) {
    		mailAddress = "";
    	}
        this.mailAddress = mailAddress;
    }
        
    /**
     * @return ���� accountDept��
     */
    public String getAccountDept() {
        return accountDept;
    }

    /**
     * @param accountDept Ҫ���õ� accountDept��
     */
    public void setAccountDept(String accountDept) {
        this.accountDept = accountDept;
    }
    
    /**
     * @return ���� accountSection��
     */
    public String getAccountSection() {
        return accountSection;
    }

    /**
     * @param accountSection Ҫ���õ� accountSection��
     */
    public void setAccountSection(String accountSection) {
        this.accountSection = accountSection;
    }
    
    /**
     * @return ���� accountPos��
     */
    public String getAccountPos() {
        return accountPos;
    }

    /**
     * @param accountPos Ҫ���õ� accountPos��
     */
    public void setAccountPos(String accountPos) {
        this.accountPos = accountPos;
    }

}
