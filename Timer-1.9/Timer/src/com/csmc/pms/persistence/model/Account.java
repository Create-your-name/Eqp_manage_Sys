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
     * @return 返回 accountNo。
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo 要设置的 accountNo。
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    /**
     * @return 返回 accountName。
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @param accountName 要设置的 accountName。
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }    
   
    /**
     * @return 返回 mailAddress。
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * @param mailAddress 要设置的 mailAddress。
     */
    public void setMailAddress(String mailAddress) {
    	if(mailAddress == null) {
    		mailAddress = "";
    	}
        this.mailAddress = mailAddress;
    }
        
    /**
     * @return 返回 accountDept。
     */
    public String getAccountDept() {
        return accountDept;
    }

    /**
     * @param accountDept 要设置的 accountDept。
     */
    public void setAccountDept(String accountDept) {
        this.accountDept = accountDept;
    }
    
    /**
     * @return 返回 accountSection。
     */
    public String getAccountSection() {
        return accountSection;
    }

    /**
     * @param accountSection 要设置的 accountSection。
     */
    public void setAccountSection(String accountSection) {
        this.accountSection = accountSection;
    }
    
    /**
     * @return 返回 accountPos。
     */
    public String getAccountPos() {
        return accountPos;
    }

    /**
     * @param accountPos 要设置的 accountPos。
     */
    public void setAccountPos(String accountPos) {
        this.accountPos = accountPos;
    }

}
