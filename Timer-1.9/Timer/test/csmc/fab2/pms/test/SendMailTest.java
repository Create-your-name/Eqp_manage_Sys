package csmc.fab2.pms.test;

import com.csmc.pms.service.support.PromisSupport;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.csmc.util.Constants;
import com.csmc.util.mail.MailManager;

public class SendMailTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        FileSystemXmlApplicationContext ctx = null;
        try {
            ctx = new FileSystemXmlApplicationContext(Constants.MAIL_CONFIG_DIR);


            MailManager mailManager = (MailManager) ctx.getBean("mailManager");

            mailManager.sendMail("liuhai82@rxgz.crmicro.com", "liuhai82@rxgz.crmicro.com", "test: –¬” œ‰", "–¬” œ‰");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
    }



}
