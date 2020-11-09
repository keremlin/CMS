package ara;

import ara.models.*;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Slf4j
@Controller

public class HomeController implements WebMvcConfigurer {

    @Autowired
    private ara.repository.documentRepository repoDocument;
    @Autowired
    private ara.repository.workgroupRepository repoWorkGoup;
    @Autowired
    private ara.repository.UserRepository repoUser;
    @Autowired
    private ara.repository.LinkerRepository repoLinker;
    @Autowired 
    private ara.repository.hierarchyRepository repoHierarchy;

    @Value("${ara.homePagging}")
    private int homePagging;
    @Value("${ara.amarGroupId}")
    private long amarGroupId;

    public boolean isAmar(){
        return (repoWorkGoup.findByIdAndMembers(amarGroupId, repoUser.findByUsername(getCurrentUserName())).size() > 0 ? true :false);
    }
    private User currentUser(){
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
    private Model isAuth(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true) {
            model.addAttribute("msg", "کاربر میهمان");
            model.addAttribute("isAuth", "F");
        }
        else{
            model.addAttribute("msg",authentication.getName());
            model.addAttribute("isAuth","T");
        }
        return model;
    }
    private boolean isAuthenticated(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.getName()!=null
                && authentication.getName().startsWith("anonymousUser")!=true) {
            return true;
        }
        return false;

    }
    private List<document> selectOption(int option,int type){
        //get current user
        User CurrentUser=currentUser();
        List<document> temp=new ArrayList<>();

        //select functionality
        switch(option) {
            case 1://new directs
                if(isCurrentUserManagerAndHigher()){
                    temp = repoDocument.findAllByWorkGroupsIsInAndDirectAndCreateDateGreaterThanOrderByEditDateDesc(
                            CurrentUser.getWorkGroups(),
                            true,
                            CurrentUser.getPreviousLogin()
                    );
                }
                else {
                    temp = repoDocument.findAllByOwnerAndDirectAndCreateDateGreaterThanOrderByEditDateDesc(
                            CurrentUser,
                            true,
                            CurrentUser.getPreviousLogin()
                    );
                }
                break;
            case 2://new documents
                temp= repoDocument.findAllByWorkGroupsIsInAndEnabledAndCreateDateGreaterThanOrderByEditDateDesc(
                        CurrentUser.getWorkGroups(),
                        true,
                        CurrentUser.getPreviousLogin()
                );
                break;
            case 3://new comment
                temp= repoDocument.findAllByOwnerAndDirectAndCreateDateGreaterThanOrderByEditDateDesc(
                        CurrentUser,
                        true,
                        CurrentUser.getPreviousLogin()
                );
                break;
            case 4:// documents
                temp= repoDocument.findAllByWorkGroupsAndEnabledOrderByEditDateDesc(
                        CurrentUser.getWorkGroups().iterator().next(),
                        true
                );
                break;
            case 5:// documents
                temp= repoDocument.findAllByOwnerAndDirectAndCreateDateGreaterThanOrderByEditDateDesc(
                        CurrentUser,
                        true,
                        CurrentUser.getPreviousLogin()
                );
                break;
            case 6:// your documents
                temp= repoDocument.findAllByOwnerOrderByEditDateDesc(
                        CurrentUser);
                break;
            case 7:// hierarchy option
                List<hierarchy> tempHierarchy=repoHierarchy.findHierarchyByLinker(repoLinker.findLinkerById((long)type));
                for(hierarchy item : tempHierarchy){
                    document tempDoc=new document();
                    tempDoc.setTitle(item.getTitle());
                    tempDoc.setId(item.getId());
                    temp.add(tempDoc);
                    
                }
                break;
            case 8:
                temp=repoDocument.findAllByHierarchyOrderByEditDateDesc(repoHierarchy.findHierarchyById((long)type));
                break;
            default:
                // code block
        }
        return temp;
    }
    @GetMapping("/more")
    public String more(Model model,
                       int type,
                       @RequestParam(value = "option",defaultValue = "-1") int option){
        //send message to view
        model=isAuth(model);
        model.addAttribute("pageTitle","مطالب");
        ara.models.User user2;

        if(option>0){// if option  is enabled
            model.addAttribute("publicDocument", selectOption(option,type));
        }
        //check if type is public or not
        else if(type==0) {
            //if user is authenticated
            if (isAuthenticated() == true) {
                user2 = repoUser.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName());
                List<document> temp = repoDocument.findAllByWorkGroupsAndEnabledOrderByEditDateDesc(repoWorkGoup.findByName("عمومی"),true);
                temp.addAll(repoDocument.findAllByWorkGroupsIsInAndEnabledOrderByEditDateDesc(user2.getWorkGroups(),true));
                model.addAttribute("publicDocument", temp);
            } else {
                model.addAttribute("publicDocument",
                        repoDocument.findAllByWorkGroupsAndEnabledOrderByEditDateDesc(repoWorkGoup.findByName("عمومی"),true));
            }
        }
        else{
            model.addAttribute("publicDocument",
                    repoDocument.findAllByWorkGroupsOrderByEditDateDesc(repoWorkGoup.findById(type)));
        }


        return "more";
    }
    @GetMapping("/")
    public String home(Model model) {
        //send message to view
        model=isAuth(model);
        model.addAttribute("pageTitle","صفحه اول");
        model.addAttribute("isAmar",isAmar());

        List<document> documents=null;
        //find all public document;

        Pageable documentPagable = PageRequest.of(0, homePagging);

        model.addAttribute("servicesList",
                repoDocument.findAllByWorkGroupsAndEnabledOrderByEditDateDesc(repoWorkGoup.findByName("سرویس"),documentPagable,true));

        model.addAttribute("linkerList",
                repoLinker.findAll());

        Page<document> pagesOfDocument=
                repoDocument.findAllByWorkGroupsAndEnabledOrderByEditDateDesc(repoWorkGoup.findByName("عمومی"),documentPagable,true);
        List<document> tempo=pagesOfDocument.getContent();

        if(isAuthenticated()==true) {
            ara.models.User user2 = repoUser.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName());

            pagesOfDocument=
              repoDocument.findAllByWorkGroupsIsInAndEnabledOrderByEditDateDesc(user2.getWorkGroups(),documentPagable,true);
            documents=pagesOfDocument.getContent();

             //documents.addAll(tempo);
             List<document> veryTemp =new ArrayList<>();
             veryTemp.addAll(documents);
             veryTemp.addAll(tempo);
            model.addAttribute("publicDocument",veryTemp);
        }
        else
            model.addAttribute("publicDocument", tempo);

        log("## Home ##"," User : "+getCurrentUserName());
            return "home";
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        model=isAuth(model);
        byte errorCode=0;
        int flag=0;
        String errorMessage = null;
        if(error != null) {
            errorMessage = "نام کاربری یا رمز عبور اشتباه است";
            flag=1;
            errorCode=1;
        }else{
            errorMessage="ok";
            errorCode=0;
        }
        if(logout != null) {
            errorMessage = "خروج با موفقیت انجام شد";
            flag=2;
        }
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("flag",flag);
        model.addAttribute("pageTitle","ورود به سامانه");
        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }


    @GetMapping("/videoConference")
    //public String testing(Model modeling){
    public String testing(Model modeling){
        //get the first document created by Skype workGroup
        Long skypeDocumentID=null;
        List<document> list=repoDocument.findAllByWorkGroups(repoWorkGoup.findByName("اسکایپ"));

        if(list.size()>0) {
            skypeDocumentID=list.get(0).getId();
            return "redirect:admin/editor/showPost?documentId=" + skypeDocumentID;
        }
        else{
            return "redirect:";
        }
    }
    @GetMapping("/linker")
    public String linker(Model modeling,Long key){

        linker Linker=repoLinker.findLinkerById(key);
        Long DocumentID=null;
        List<hierarchy> hierarchy=repoHierarchy.findHierarchyByLinker(Linker);
        List<document> list=repoDocument.findAllByWorkGroups(Linker.getWorkGroups());

        if(hierarchy.size()>0){
            return "redirect:more?type="+key+"&option=7";
        }
        else if(list.size()==1) {
            //get the first document created by a workGroup
            DocumentID=list.get(0).getId();
            return "redirect:admin/editor/showPost?documentId=" + DocumentID;
        }else if(list.size()>1){
            //else if workGroup have more than one document goto more page
            return "redirect:more?type=" + Linker.getWorkGroups().getId();
        }
        else{
            //else redirect to mainPage
            return "redirect:/";
        }
    }
    @GetMapping("/downloadVideoConference")
    public String downloadVideoConference(Model model){
        model.addAttribute("pageTitle","دانلود ویدیو جلسات اسکایپ ");
        model=isAuth(model);

        if(isAuthenticated()==true) {
            model.addAttribute("documentList",
                    repoDocument.findAllByWorkGroups(repoWorkGoup.findByName("اسکایپ-دانلود")));

        }
        return "downloadVideoConference";
    }
    @GetMapping("/401")
    public String p401(Model model){
        model.addAttribute("pageTitle","401 ورود غیر مجاز ");
        model=isAuth(model);
        log("## 401 ##"," User : "+getCurrentUserName());
        return "401";
    }
    @RequestMapping(value = "/testForm", method = RequestMethod.POST)
    public  String testingPost(@ModelAttribute @Valid viewmodel model, BindingResult bindingResult,Model modeling){
        return  ("redirect:/");
    }

    private void log(String  head,String log){
        Calendar cal = Calendar.getInstance();
        System.out.println(head + " #"+cal.getTime()+" : "+log);
        return;
    }
    public static String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
}

}
