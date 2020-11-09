package ara.auth;

import ara.models.usersHistory;
import ara.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Calendar;

@Component
public class AuthenticationEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

    //private static Logger logger = Logger.getLogger();
    @Autowired
    private HistoryRepository repoUserHistory;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent authenticationEvent) {
        if (authenticationEvent instanceof InteractiveAuthenticationSuccessEvent) {
            // ignores to prevent duplicate logging with AuthenticationSuccessEvent
            return;
        }
        Authentication authentication = authenticationEvent.getAuthentication();
         
        String auditMessage = "Login:\"" + authentication.isAuthenticated()+"\"" ;
        repoUserHistory.save(userHistory("## AUDI ##"," User : "+" " +auditMessage,authentication.getName(),1));
    }

    public static usersHistory userHistory(String  head,String log,String userName,int level){
        Calendar cal = Calendar.getInstance();
        System.out.println(head + " #"+cal.getTime()+" : "+" User : "+userName+" "+log);
        usersHistory login=new usersHistory();
        login.setCreateDate(cal.getTime());
        login.setLogLevel(level);
        login.setLogMessage(log);
        login.setLogIp( ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr());
        login.setUserName(userName);
        //repoUserHistory.save(login);
        return login;
    }
   

}
