package ara.auth;
import ara.models.User;
import ara.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthenticationSuccessEventListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserRepository repoUser;

    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        /*WebAuthenticationDetails auth = (WebAuthenticationDetails)
                e.getAuthentication().getDetails();*/
        User user=repoUser.findByUsername(e.getAuthentication().getName());
        if(user != null)
        {
            user.setTries((long)0);
            user.setLastIpAddress(((WebAuthenticationDetails) e.getAuthentication().getDetails()).getRemoteAddress());
            user.setPreviousLogin(user.getLastLogin());
            user.setLastLogin(new Date());
            repoUser.save(user);
        }

    }
}
