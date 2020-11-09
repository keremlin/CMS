package ara;

import ara.models.User;
import ara.models.vmStatus;
import ara.repository.UserRepository;
import ara.repository.documentCommentRepository;
import ara.repository.documentRepository;
import ara.repository.workgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
public class panelController  implements WebMvcConfigurer {

    @Autowired
    private workgroupRepository repoWorkgroup;
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private documentRepository repoDocument;
    @Autowired
    private documentCommentRepository repoComment;

    // check how many Group a user have
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfGroups")
    public vmStatus numberOfGroups() {

           int temp= repoWorkgroup.countAllByMembersContains(currentUser());
        return new vmStatus(){{
            setCode(temp);
            setMessage("تعداد زیاد");
        }};
    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfDocuments")
    public vmStatus numberOfDocuments() {
    int temp=repoDocument.countAllByOwner(currentUser());
        return new vmStatus(){{
        setCode(temp);
    }};

    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfComments")
    public vmStatus numberOfComments() {
        int temp=repoComment.countAllByUser(currentUser());
        return new vmStatus(){{
            setCode(temp);
        }};

    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfNewDocuments")
    public vmStatus numberOfNewDocuments() {

        int temp=repoDocument.countAllByWorkGroupsIsInAndEnabledAndCreateDateGreaterThan(
                repoWorkgroup.findByMembers(currentUser()),true,
                repoUser.findUserById(currentUser().getId()).getPreviousLogin()
        );
        return new vmStatus(){{
            setCode(temp);
        }};

    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfGroupDocuments")
    public vmStatus numberOfGroupDocuments() {
        int temp=repoDocument.countAllByWorkGroupsIsInAndEnabled(repoWorkgroup.findByMembers(currentUser()),true);
        return new vmStatus(){{
            setCode(temp);
        }};

    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfNewDirect")
    public vmStatus numberOfNewDirect() {
        int temp=0;
        if(isCurrentUserManagerAndHigher()==false) {//if role user
            temp = repoDocument.countAllByOwnerAndDirectAndCreateDateGreaterThan(
                    currentUser(),
                    true,
                    repoUser.findUserById(currentUser().getId()).getPreviousLogin()
            );
        }else// if role higher than user
        {
            temp = repoDocument.countAllByWorkGroupsIsInAndDirectAndCreateDateGreaterThan(
                    currentUser().getWorkGroups(),
                    true,
                    repoUser.findUserById(currentUser().getId()).getPreviousLogin()
            );
        }
        final int temp2=temp;
        return new vmStatus(){{
            setCode(temp2);
        }};

    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/panel/numberOfNewComments")
    public vmStatus numberOfNewComments() {
        int temp=repoComment.countAllByDocumentIsInAndEnabledAndCreateDateGreaterThan(
                repoDocument.findAllByOwner(currentUser()),
                true,
                currentUser().getPreviousLogin()
                );
        return new vmStatus(){{
            setCode(temp);
        }};

    }
    //return current user object
    private User currentUser(){
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repoUser.findUserByUsername(authentication.getName());
    }
    private boolean isCurrentUserManagerAndHigher(){
        String roleName=currentUser().getRoles().iterator().next().getName();

        if(roleName.startsWith("ROLE_ADMIN")||roleName.startsWith("ROLE_MANAGER"))
            return true;
        else
            return false;
    }
}
