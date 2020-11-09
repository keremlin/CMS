package ara.auth;

import ara.models.User;
import ara.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Value("${ara.userTries}")
    private int userTries;

    @Autowired
    private UserRepository repoUser;

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        WebAuthenticationDetails auth = (WebAuthenticationDetails)
                e.getAuthentication().getDetails();
        //find user try to login
        User user=repoUser.findUserByUsername(e.getAuthentication().getName());

        if(user!=null ) {
           // default user number of tries is 0
            if(user.getTries()==null || user.getTries()<0)
                user.setTries((long)0);
            //set IP of source
            user.setLastIpAddress(auth.getRemoteAddress());
            // increase the number of tries +1
            user.setTries(user.getTries() + 1);
            //if number of user tries is more than a number lock user
            if(user.getTries()>=userTries){
                user.setEnabled(false);
            }
            // save to DB
            repoUser.save(user);
        }
    }
}
