package ara;

import ara.auth.AuthenticationEventListener;
import ara.excel.writeExcel;
import ara.models.*;
import ara.repository.RoleRepository;
import ara.repository.documentRepository;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Controller
public class AdminController implements WebMvcConfigurer {


    @Autowired
    private RoleRepository repoRole;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ara.repository.workgroupRepository repoWorkGroup;
    @Autowired
    private documentRepository repoDocument;
    @Autowired
    private ara.repository.UserRepository repoUser;
    @Autowired
    private ara.repository.documentFileRepository repoFileDocument;
    @Autowired
    private ara.repository.HistoryRepository repoHistoryRepository;
    @Autowired
    private  ara.repository.LinkerRepository repoLinker;
    @Autowired
    private ara.repository.hierarchyRepository repoHierarchy;
    @Autowired
    private ara.repository.olapIndexRepository repoOlapIndex;
    @Autowired
    private ara.repository.olapRepository repoOlap;
    @Autowired
    private ara.repository.olapUserTownRepository repoOlapUserTown;
    
    


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

    private Model getLoggedUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        model.addAttribute("userRole",authorities.iterator().next().getAuthority());
        return model.addAttribute("loggedUser",authentication.getName());
    }
    public static String getCurrentUserName(){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication.getName();
    }
   

    @Value("${ara.documentPagging}")
    private int documentPagging;

    @Value("${ara.userPagging}")
    private int userPagging;

    @Value("${ara.amarGroupId}")
    private long amarGroupId;

    @Value("${ara.uploadExcel}")
    private String uploadExcel;

    public boolean isAmar(){
        return (repoWorkGroup.findByIdAndMembers(amarGroupId, repoUser.findByUsername(getCurrentUserName())).size() > 0 ? true :false);
    }
    @GetMapping("/admin/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true)
        {
            return "/login";
        }
        //find out current user and his rule
        model=getLoggedUser(model);
        model=isAuth(model);
        model.addAttribute("pageTitle","ادمین");

        //Get users list by paging function
        Pageable userPagable = PageRequest.of(0, userPagging);
        Page<User> pagesOfUsers;
        pagesOfUsers=repoUser.findAll(userPagable);
        List<vmUser> vmuser;

        vmuser=vmUser.createList(pagesOfUsers.getContent(),pagesOfUsers.getNumber(),pagesOfUsers.getTotalPages());

        model.addAttribute("usersAllPages",pagesOfUsers.getTotalPages());


        //Get workGroup
        ArrayList<workGroup> workgroup=repoWorkGroup.findAll();
        ArrayList<vmWorkGroup> tempWorkGroup=new ArrayList<>();
        for(workGroup item:workgroup){
            String tempMembers="";

            //list all member and put it in a string
            for(User item2 :item.getMembers())
                tempMembers+="{"+item2.getUsername()+"}";

            //check if workGroup is enabled or not
            String tempIsEnabled="غیرفعال";
            if(item.isEnabled()==true)
                tempIsEnabled="فعال";

            tempWorkGroup.add(new vmWorkGroup(item.getId(),item.getName(),
                    item.getDescription(),item.getOwner().getUsername(),tempMembers,tempIsEnabled,""));
        }

        List<vmDocuments> tempDocuments=new ArrayList<vmDocuments>();
        List<document> tempDocumentList;


        Pageable firstPageWithTwoElements = PageRequest.of(0, documentPagging);
        //repoDocument.findAll(firstPageWithTwoElements);

        Page<document> pagesOfDoccuments;
        if(authentication.getName().startsWith("Admin")==true) {
            pagesOfDoccuments=repoDocument.findAll(firstPageWithTwoElements);

        }
        else{
            pagesOfDoccuments=repoDocument.findAllByOwner(repoUser.findUserByUsername(authentication.getName()),firstPageWithTwoElements);
        }
        tempDocumentList=pagesOfDoccuments.getContent();
        model.addAttribute("documentAllPages",pagesOfDoccuments.getTotalPages());

        tempDocuments=vmDocuments.createList(tempDocumentList,0,pagesOfDoccuments.getTotalPages());
        //find all roles
        List<Role> tempAllRoles=repoRole.findAll();

        // check which user try to be admin

        //String currentPrincipalName = authentication.getName()
        //        + authentication.getAuthorities() + authentication.getCredentials() + authentication.getDetails() +
        //        authentication.getPrincipal();
        //send message to view
        boolean isAdmin=true;
         if(authentication.getName().startsWith("Admin")!=true)
        {
            tempWorkGroup=null;
            vmuser=null;
            tempAllRoles=null;
            isAdmin=false;

        }
        // check if user is amar user
        model.addAttribute("isAmar",isAmar());
        //log("is AMAR ",(repoWorkGroup.findByIdAndMembers(amarGroupId, repoUser.findByUsername(getCurrentUserName())).size() > 0 ? true :false) +"");
        //then send lists base on user
        model.addAttribute("isAdmin",isAdmin);
        model.addAttribute("documentsList",tempDocuments);
        model.addAttribute("workGroupList",tempWorkGroup);
        model.addAttribute("allUsers",vmuser);
        model.addAttribute("isEmpty",pagesOfUsers.getContent().isEmpty());
        model.addAttribute("roleList",tempAllRoles);
        return "admin/home";
    }
    @GetMapping("/admin/fileManagement")
    public String fileManagement(Model model){
        
        model=getLoggedUser(model);
        model=isAuth(model);
        model.addAttribute("linkList",repoOlapIndex.findAll());
        model.addAttribute("isAmarAdmin",
            (
                repoWorkGroup.findByIdAndOwner(amarGroupId ,repoUser.findByUsername(getCurrentUserName())) != null ? true :false
            ) | (
                getCurrentUserName().startsWith("Admin")
                )
        );
        log("## Report management ##","User : "+getCurrentUserName());
        return "admin/fileManagement";

    }
    @GetMapping("/admin/userSetting")
    public String userSetting(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true)
        {
            return "/login";
        }
        model=getLoggedUser(model);
        model=isAuth(model);
        model.addAttribute("pageTitle","تنظیمات کاربران");
        model.addAttribute("roleList",repoRole.findAll());
       
        return "admin/userSetting";
    }
    @GetMapping("/admin/documentManagement")
    public String documentManagement(Model model,@RequestParam(value = "groupId",defaultValue = "-1") long groupId,
                                     @RequestParam(value = "userId",defaultValue = "-1") long userId){
        //get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());
        //check if user not logged in
        if(authentication.getName().startsWith("anonymousUser")==true)
        {
            return "/login";
        }

        //send models
        model=getLoggedUser(model);
        model=isAuth(model);
        model.addAttribute("pageTitle","تنظیمات کاربران");

        List<vmDocuments> tempDocuments=new ArrayList<vmDocuments>();
        List<document> tempDocumentList;


        Pageable firstPageWithTwoElements = PageRequest.of(0, documentPagging);
        //repoDocument.findAll(firstPageWithTwoElements);

        Page<document> pagesOfDoccuments=null;
        // hold documents owners to search by
        List<document> searchByPeopleDocuments=null;

        if(authentication.getName().startsWith("Admin")==true) {

            //TODO: change the query to return only users' name
            searchByPeopleDocuments=repoDocument.findAll();
            if(userId>0)
                pagesOfDoccuments=repoDocument.findAllByOwnerOrderByEditDateDesc(repoUser.findUserById(userId),firstPageWithTwoElements);
            else if(groupId<0)
                pagesOfDoccuments = repoDocument.findAllByOrderByEditDateDesc(firstPageWithTwoElements);
            else
                pagesOfDoccuments=repoDocument.findAllByWorkGroupsOrderByEditDateDesc(repoWorkGroup.findById(groupId),firstPageWithTwoElements);

        }
        else if(currentUser.getRoles().contains(repoRole.findByName("ROLE_FTP"))){
            User tempUser =repoUser.findUserByUsername(authentication.getName());

            //TODO: change the query to return only users' name
            searchByPeopleDocuments=repoDocument.findAllByOwner(tempUser);

            pagesOfDoccuments=repoDocument.findAllByOwnerOrderByEditDateDesc(
                    tempUser,firstPageWithTwoElements);
        }
        else if(currentUser.getRoles().contains(repoRole.findByName("ROLE_MANAGER"))){
            ArrayList<workGroup> tempList = repoWorkGroup.findByOwner(repoUser.findUserByUsername(authentication.getName()));
            //TODO: change the query to return only users' name
            searchByPeopleDocuments=repoDocument.findAllByWorkGroupsIsIn(tempList);
            if(userId<0)
                pagesOfDoccuments=repoDocument.findAllByWorkGroupsIsInOrderByEditDateDesc(
                    tempList,
                    firstPageWithTwoElements);
            else
                pagesOfDoccuments=repoDocument.findAllByOwnerAndWorkGroupsIsInOrderByEditDateDesc(
                        repoUser.findUserById(userId),
                        tempList,
                        firstPageWithTwoElements);
        }
        //check for ftp users to add more groups
        ArrayList<workGroup> listOfFtpGroups=repoWorkGroup.findByMembersAndFtpGroup(currentUser,true);
        listOfFtpGroups.addAll(repoWorkGroup.findByOwner(currentUser));

        tempDocuments.stream().distinct().collect(Collectors.toList());


        tempDocumentList=pagesOfDoccuments.getContent();
        tempDocuments=vmDocuments.createList(tempDocumentList,0,pagesOfDoccuments.getTotalPages());

        ArrayList<valueAndKey> allPersonInGroup=new ArrayList<>();
        for(document item : searchByPeopleDocuments)
        {

            valueAndKey tempUserValueAndKey= new valueAndKey()
            {{
                setValue(item.getOwner().getLastName() + " " + item.getOwner().getFirstName());
                setKey(item.getOwner().getId());
            }};
            if(!allPersonInGroup.contains(tempUserValueAndKey)) {
               allPersonInGroup.add(tempUserValueAndKey);
            }


        }

        model.addAttribute("documentAllPages",pagesOfDoccuments.getTotalPages());
        model.addAttribute("allPersonInGroup",allPersonInGroup);
        model.addAttribute("documentsList",tempDocuments);
        model.addAttribute("allGroups",listOfFtpGroups);
        model.addAttribute("workGroupId",groupId);
        model.addAttribute("userId",userId);
        model.addAttribute("currentUserRole",currentUser.getRoles().iterator().next().getName());

        return "admin/documentManagement";
    }
    @GetMapping("/admin/linkerManagement")
    public String linkerManagement(Model models){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true)
            return "/login";

    models.addAttribute("linkList",repoLinker.findAll());

    models=getLoggedUser(models);
    models=isAuth(models);
    models.addAttribute("pageTitle","تنظیمات لینک ها");

        return "admin/linkerManagement";
    }
    @GetMapping("/admin/hierarchyManagement")
    public String hierarchyManagement(Long id,Model models){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true)
            return "/login";

    models.addAttribute("linkList",repoHierarchy.findHierarchyByLinker(repoLinker.findById(id).get()));
    models.addAttribute("linkerID",id);

    models=getLoggedUser(models);
    models=isAuth(models);
    models.addAttribute("pageTitle","تنظیمات سلسله مراتب");

        return "admin/hierarchyManagement";
    }
    @GetMapping("/admin/groupManagement")
    public String groupManagement(Model model, long ownerId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true)
        {
            return "/login";
        }
        model=getLoggedUser(model);
        model=isAuth(model);
        model.addAttribute("pageTitle","تنظیمات گروه کاری");
        //Get workGroup
        ArrayList<workGroup> workgroup;

        if(ownerId==-1)
            workgroup=repoWorkGroup.findAll();
        else
            workgroup=repoWorkGroup.findByOwner(repoUser.findUserById(ownerId));


        ArrayList<vmWorkGroup> tempWorkGroup=new ArrayList<>();
        for(workGroup item:workgroup){
            String tempMembers="";

            //list all member and put it in a string
            for(User item2 :item.getMembers())
                tempMembers+="{"+item2.getUsername()+"}";

            //check if workGroup is enabled or not
            String tempIsEnabled="غیرفعال";
            if(item.isEnabled()==true)
                tempIsEnabled="فعال";
            String tempISpublic="اختصاصی";
            if(item.isAnyOneGroup())
                tempISpublic="عمومی";

            tempWorkGroup.add(new vmWorkGroup(item.getId(),item.getName(),
                    item.getDescription(),item.getOwner().getUsername(),tempMembers,tempIsEnabled,tempISpublic));
        }
        model.addAttribute("workGroupList",tempWorkGroup);
        model.addAttribute("filterId",ownerId);
        model.addAttribute("allManagers",repoUser.findAllByRoles(repoRole.findByName("ROLE_MANAGER")));
        return "admin/groupManagement";
    }
    @GetMapping("/admin/userManager")
    public String userManager(Model model, @RequestParam(value = "groupId",defaultValue = "-1") long groupId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().startsWith("anonymousUser")==true)
        {
            return "/login";
        }
        //find out current user and his rule
        model=getLoggedUser(model);
        model=isAuth(model);
        model.addAttribute("pageTitle","مدیریت کاربران");

        //Get users list by paging function
        Pageable userPagable = PageRequest.of(0, userPagging);
        Page<User> pagesOfUsers;

        if(groupId<0)
            pagesOfUsers=repoUser.findAll(userPagable);
        else
            pagesOfUsers=repoUser.findUserByWorkGroups(userPagable,repoWorkGroup.findById(groupId));

        List<vmUser> vmuser;
        vmuser=vmUser.createList(pagesOfUsers.getContent(),pagesOfUsers.getNumber(),pagesOfUsers.getTotalPages());
        model.addAttribute("allUsers",vmuser);
        model.addAttribute("usersAllPages",pagesOfUsers.getTotalPages());
        model.addAttribute("workGroupId",groupId);

        // get all groups
        model.addAttribute("allGroups",repoWorkGroup.findAll());

        return "admin/userManager";
    }
    @GetMapping("/admin/removeUser")
    @Transactional()
    public String removeUser(long id) {

        try {
            //find the user
            User user=repoUser.findUserById(id);
            //if the user has not a group
            if(user.getOwnWorkGroup().size()<=0)
            {
                //find all the user's workGroups
                Collection<workGroup> workgroup=user.getWorkGroups();
                //for each workGroup that user is exist
                for(workGroup item :workgroup){
                    //remove the user from the list of that workGroup
                    ArrayList<User> tempList=new ArrayList<>(item.getMembers());
                    tempList.remove(user);
                    item.setMembers(tempList);
                    repoWorkGroup.save(item);
                }
                //finally remove the user
                repoUser.removeUserById(id);
                log("## Remove User ##"," User : "+getCurrentUserName()+ "User was Removed : " + id  );
            }
            else{log("## Remove User ##", "User was NOT Removed : " + id  );}

            return ("redirect:/admin/userManager");
        }
        catch(DataAccessException e){

            log("## Exception ##","user was NOT removed : Exception" +e.getMessage());
            return ("redirect:/admin/");
        }
    }
    @GetMapping("/admin/removeWorkGroup")
    @Transactional
    public String removeWorkGroup(long id,int filterId){
        try {
            log("## WorkGroup is removed ##"," User :"+getCurrentUserName()+ "Removed : " + id + " " + repoWorkGroup.removeById(id));
            return ("redirect:/admin/groupManagement?ownerId="+filterId);
        }
        catch(Exception ex){
            log("## EXCEPTION ##","WorkGroup was NOT removed: Exception "+ex.getMessage());
            return ("redirect:/admin/groupManagement?ownerId="+filterId);
        }
    }
    @GetMapping("/admin/removeHierarchy")
    @Transactional
    public String removeHierarchy(Long id,Long linkerId){
        try{

            log("## Hierarchy is removed ##"," User : "+getCurrentUserName()+ "Removed : " + id + " " +repoHierarchy.removeById(id));
        }
        catch(Exception ex){log("##  ERROR EXCEPTION ##","linker (id:"+id+") was NOT removed: Exception "+ex.getMessage());}
        return "redirect:/admin/hierarchyManagement?id="+linkerId;
    }
    @GetMapping("/admin/removeLink")
    @Transactional
    public String removeLink(long id){
        try{

            log("## Linker is removed ##"," User : "+getCurrentUserName()+ "Removed : " + id + " " +repoLinker.removeById(id));
        }
        catch(Exception ex){log("##  ERROR EXCEPTION ##","linker (id:"+id+") was NOT removed: Exception "+ex.getMessage());}
        return "redirect:/admin/linkerManagement";
    }
    @GetMapping("/admin/removeExcel")
    @Transactional
    public String removeExcel(long id){
        try{
            
            
            repoOlap.removeByOlapIndexer(repoOlapIndex.findOlapIndexById(id));
            repoOlapIndex.removeOlapIndexById(id);
            log("## Excel is removed ##"," User : "+getCurrentUserName()+ " Removed : " + id + " ");
        }
        catch(Exception ex){log("##  ERROR EXCEPTION ##","linker (id:"+id+") was NOT removed: Exception "+ex.getMessage());}
        return "redirect:/admin/fileManagement";
    }
    @GetMapping("/admin/bannUser")
     public String bannUser(long id){
        
        User user=repoUser.findUserById(id);
        user.setEnabled(!user.isEnabled());
        repoUser.save(user);
        return "redirect:/admin/userManager";
    }
    @GetMapping("/admin/bannWorkGroup")
    public String bannWorkGroup(long id,int filterId){
        
        workGroup user=repoWorkGroup.findById(id);
        user.setEnabled(!user.isEnabled());
        repoWorkGroup.save(user);
        return "redirect:/admin/groupManagement?ownerId="+filterId;
    }

    @GetMapping("/admin/publicizeWorkGroup")
    public String publicizeWorkGroup(long id,int filterId){
        workGroup user=repoWorkGroup.findById(id);
        user.setAnyOneGroup(!user.isAnyOneGroup());
        repoWorkGroup.save(user);
        return "redirect:/admin/groupManagement?ownerId="+filterId;
    }
    @GetMapping("/admin/editUser")
    public String editUser(Model model,long id){
        boolean isNotAdmin=false;
        model=isAuth(model);
        model=getLoggedUser(model);
        
        
        // change the messsage of user
        if(id==-1){
            model.addAttribute("error",false);
            model.addAttribute("errorMessage","رمز عبور خود را تغییر دهید");   
        }else if(id==-2)
        {
            model.addAttribute("error",false);
            model.addAttribute("errorMessage","رمز عبور با موفقیت تغییر کرد");
        }else if(id==-3){
            model.addAttribute("error",true);
            model.addAttribute("errorMessage","خطا در تغییر رمز عبور");  
        }
        // check if password changing is clicked.
        if(id<0)
        {
            id=repoUser.findByUsername( getCurrentUserName()).getId();
            isNotAdmin=true;
        }
            
        // show other field than password field
        model.addAttribute("isAdmin",isNotAdmin);         
        model.addAttribute("obj",repoUser.findUserById(id));
        
        return "admin/editUser";
    }
    @GetMapping("/admin/editWorkGroup")
    public String editWorkGroup(Model model ,long id){
        model=isAuth(model);
        model=getLoggedUser(model);

        model.addAttribute("wg",id);
        //model.addAttribute("userid",id);
        ArrayList<Long> tempid=new ArrayList<>();
        Collection<User> members=repoWorkGroup.findById(id).getMembers();
        for(User user : members)
        {
            tempid.add(user.getId());
        }
        model.addAttribute("allUser",repoUser.findAllByIdNotIn(tempid));
        model.addAttribute("addedUsers",members);
        return "admin/editWorkGroup";
    }
    @GetMapping("/admin/olapGroups")
    public String olapGroups(Model model ,long id, HttpServletRequest request){
        
        model=isAuth(model);
        log(" this x-forward : ", request.getRemoteAddr());
        model=getLoggedUser(model);

        model.addAttribute("wg",amarGroupId);
        //model.addAttribute("userid",id);
       
        Collection<User> members=repoWorkGroup.findById(amarGroupId).getMembers();
        List<olap> towns=repoOlap.findAll();
        List<String> newTowns = new ArrayList(); //towns.stream() 
                                //      .distinct() 
                                //      .collect(Collectors.toList());
        for(olap item:towns)
            if(item.getD1()!=null && item.getD1()!="")
                newTowns.add((item.getD1()).trim());

        newTowns=newTowns.stream().distinct().collect(Collectors.toList());
        
        
        model.addAttribute("towns",newTowns);
        model.addAttribute("addedUsers",members);
        log("## olapGroups ##","User : "+getCurrentUserName());
        return "admin/olapGroups";
    }
    @GetMapping("/admin/editor/addWorkGroupToPost")
    public String addWorkGroupToPost(Model model,long documentId){
        model=isAuth(model);
        model=getLoggedUser(model);

        
        model.addAttribute("documentId",documentId);
        ArrayList<Long> tempid=new ArrayList<>();
        for(workGroup workGroups : repoDocument.findDocumentById(documentId).getWorkGroups())
        {
            tempid.add(workGroups.getId());
        }
        model.addAttribute("workGroup",repoWorkGroup.findAllByIdNotIn(tempid));
        return"admin/addWorkGroupToPost";
    }
    @GetMapping("/admin/addUserToWorkGroup")
    public String addUserToWorkGroup(long id,long wg,Model model){
        model=isAuth(model);
        model=getLoggedUser(model);
        ara.models.workGroup tempWorkGroup =repoWorkGroup.findById(wg);
        User tempUser =repoUser.findUserById((int)id);
        ArrayList<User> tempList=new ArrayList<>(tempWorkGroup.getMembers());
        tempList.add(tempUser);
       tempWorkGroup.setMembers(tempList);
       repoWorkGroup.save(tempWorkGroup);
        return"redirect:/admin/editWorkGroup?id="+wg;
    }
    @GetMapping("/admin/removeUserFromWorkGroup")
    public String removeUserFromWorkGroup(long id,long wg,Model model){
        model=isAuth(model);
        model=getLoggedUser(model);
        ara.models.workGroup tempWorkGroup =repoWorkGroup.findById(wg);
        User tempUser =repoUser.findUserById((int)id);
        ArrayList<User> tempList=new ArrayList<>(tempWorkGroup.getMembers());
        tempList.remove(tempUser);
        tempWorkGroup.setMembers(tempList);
        repoWorkGroup.save(tempWorkGroup);
        return"redirect:/admin/editWorkGroup?id="+wg;
    }
    @GetMapping("/admin/editor/editPost")
    public String editPost (long documentId,Model model){

        //get User
        model=isAuth(model);
        model=getLoggedUser(model);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //get workGroup for dropBox
        ArrayList<workGroup> workgroup=new ArrayList<>();
        //if admin return all workGroups
        if(authentication.getName().startsWith("Admin")==true)
            workgroup=repoWorkGroup.findAll();
        else {
            Collection<workGroup> tempWorkGroupOfOwner= repoWorkGroup.findByOwner(
                    repoUser.findUserByUsername(authentication.getName()));
            Collection<workGroup> tempWorkGroupOfFTP= repoWorkGroup.findByMembersAndFtpGroup(
                    repoUser.findUserByUsername(authentication.getName()),true);

            if(tempWorkGroupOfOwner!=null && tempWorkGroupOfOwner.size()>0)
                workgroup.addAll(tempWorkGroupOfOwner);
            if(tempWorkGroupOfFTP !=null && tempWorkGroupOfFTP.size()>0)
                workgroup.addAll(tempWorkGroupOfFTP);
        }

        ArrayList<vmWorkGroup> tempWorkGroup=new ArrayList<>();
        for(workGroup item:workgroup){
            String tempMembers="";

            //list all member and put it in a string
            for(User item2 :item.getMembers())
                tempMembers+="{"+item2.getUsername()+"}";

            //check if workGroup is enabled or not
            String tempIsEnabled="غیرفعال";
            if(item.isEnabled()==true)
                tempIsEnabled="فعال";

            tempWorkGroup.add(new vmWorkGroup(item.getId(),item.getName(),
                    item.getDescription(),item.getOwner().getUsername(),tempMembers,tempIsEnabled,""));
        }
        if(documentId==0){
            model.addAttribute("workGroupsList",tempWorkGroup);
            model.addAttribute("workGroupsListSize",tempWorkGroup.size());
        }

        else {
            //find document to load by REST , pass document id to javaScript on page and load it there
            model.addAttribute("documentId", documentId);
            model.addAttribute("workGroupsList",null);
            model.addAttribute("workGroupsListSize",0);
        }

        return "admin/editor/editPost";
    }
    @GetMapping("/admin/editor/showPost")
    public String showPost (long documentId,Model model) {


        String currentuser=getCurrentUserName();

        repoHistoryRepository.save(AuthenticationEventListener.userHistory("## UserHistory ##",
                "showPost:\""+documentId+"\"",currentuser,2));


        if(currentuser!=null  ) {
            document targetDocument= repoDocument.findDocumentById(documentId);

            Collection<workGroup> coll=null;
            if(currentuser.startsWith("anonymousUser")!=true) {
                coll = repoUser.findUserByUsername(currentuser).getWorkGroups();
            }

            //List list;
            
            List<workGroup> list2;

            if(coll==null)
                list2=null;
            else if (coll instanceof List)
                list2 = (List)coll;
            else
                list2 = new ArrayList<workGroup>(coll);

            boolean flag=false;
            if (currentuser.startsWith("Admin")) flag=true;

            for(workGroup item : targetDocument.getWorkGroups()){

                if (item.isAnyOneGroup()) flag=true;
                if(flag==true) break;
                if(list2==null) continue;
                for(workGroup matchPoint:list2)
                {
                    if(matchPoint.getId()==item.getId()) {flag=true;break;}
                }
            }
            if(flag==true) {

                model = isAuth(model);
                model = getLoggedUser(model);
                if (documentId > 0) {
                    //find document to load by REST , pass document id to javaScript on page and load it there
                    model.addAttribute("documentId", documentId);
                    return "admin/editor/showPost";

                }
            }
            else{return "redirect:/401";}
        }
        return "redirect:/401";

    }
    @GetMapping(value="/admin/editor/banPosteee")
    public vmStatus banPosteee(int documentId){
        return null;
    }
    private boolean deleteFile(documentFile doc){

        try {
            File fileToDelete = new File(doc.getPath());
            fileToDelete.delete();
            repoFileDocument.delete(doc);
            log("## PHYSICAL FILE DELETED ##"," User : "+getCurrentUserName()+" File: "+doc.getPath());

        }
        catch(Exception ex){log("## ERROR DELETE PHYSICAL FILE FAILED ##"," User : "+getCurrentUserName()+" "+ doc.getPath());return false;}
        return true;
    }

    @GetMapping(value="/admin/editor/deletePost")
    @Transactional
    public String deletePost(long documentId){


        //delete all attached physical files
        int numberOfError=0;
        ara.models.document  item =repoDocument.findDocumentById(documentId);
        for(documentFile doc : item.getDocumentFile()) {
            if(deleteFile(doc)==false) numberOfError++;
            log("## The Physical File is removed ##","User : "+getCurrentUserName()+"file : "+doc.getName()+" id: "+doc.getId());
        }

        if(numberOfError==0){
            repoDocument.deleteById(documentId);
            log("## Document is removed ##","User : "+getCurrentUserName()+" DOC  : "+documentId);
        }
        else
            log("##  ERROR POST NOT REMOVED ##"," User : "+getCurrentUserName()+item.getId()+"");



        return("redirect:/admin/documentManagement");
    }

    @RequestMapping(value="/admin/editUserPost",method=RequestMethod.POST)
    public String editUserPost(@ModelAttribute @Valid User user,BindingResult bindingResult,Model model){
        model=isAuth(model);
        if(bindingResult.hasErrors()==true){
            if(user.getPassword()!=""){
                User newUser=repoUser.findUserById(user.getId());
                newUser.setPassword(passwordEncoder.encode(user.getPassword()));
                repoUser.save(newUser);
            }
        }
        else
        {
            log("## Edit user post ##","User : " + getCurrentUserName() + user.getId());
            Collection<Role> role=repoUser.findUserById(user.getId()).getRoles();
            //log("this is roles",role.toString());
            user.setRoles(role);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            repoUser.save(user);
        }
        // check if is addmin or not
        if(repoUser.findByUsername( getCurrentUserName()).getRoles().contains(repoRole.findByName("ROLE_ADMIN")))
            return "redirect:/admin/userManager";
        else{
            return "redirect:/admin/editUser?id=-2";
        }
            
    }
    @RequestMapping(value="/admin/editor/addWorkGroupToDocument",method = RequestMethod.GET)
    public String addWorkGroupToDocument(long id,long documentId,Model model){
        model=isAuth(model);
        document tempDocument=repoDocument.findDocumentById(documentId);
        ara.models.workGroup tempWorkGroup=repoWorkGroup.findById(id);
        ArrayList<workGroup> tempWorkList=new ArrayList<>(tempDocument.getWorkGroups());
        tempWorkList.add(tempWorkGroup);
        tempDocument.setWorkGroups(tempWorkList);
        repoDocument.save(tempDocument);
        return"redirect:/admin/editor/addWorkGroupToPost?documentId="+documentId;
    }
    @GetMapping("/admin/addUser")
    public String addUser(String message,Model model){
        model=isAuth(model);
        model=getLoggedUser(model);
        model.addAttribute("obj",new User());
        model.addAttribute("allRoles",repoRole.findAll());
        model.addAttribute("message",message);
        return "admin/addUser";
    }
    @RequestMapping(value="/admin/addUserPost",method=RequestMethod.POST)
    public String addUserPost(@ModelAttribute @Valid User model,BindingResult bindingResult,Model modeling){
        modeling=isAuth(modeling);
        User checkUser=repoUser.findByUsername(model.getUsername());

        if(checkUser!=null || model.getUsername().length()<6)//|| bindingResult.hasErrors())
        {
            //log("Error Input",bindingResult.getAllErrors().get(0)+"");
            return "redirect:../admin/addUser?message=User name is NOT valid";
        }
        else
        {

            //Collection<User> temp=new LinkedList<>();
            //temp.add(repoRole.findByName("ROLE_USER"));
            ArrayList<Role> tempRole=new ArrayList<Role>();
            tempRole.add(repoRole.findRoleById(model.getId()));
            model.setId(null);
            model.setEnabled(true);
            model.setTokenExpired(false);
            model.setPassword(passwordEncoder.encode(model.getPassword()));
            model.setRoles(tempRole) ;
            model.setLastLogin(new Date());
            model.setPreviousLogin(new Date());
            repoUser.save(model);
            String messages="User successfully added.";
            return("redirect:../admin/addUser?message="+messages);
        }

    }
    @GetMapping("/admin/viewExcel")
    public String viewExcel(Long id,Model model){
        model=isAuth(model);
        //find current user and send it to Page
        model=getLoggedUser(model);
        User user=repoUser.findByUsername(getCurrentUserName());
        olapIndex tempOlapIndex=repoOlapIndex.findOlapIndexById(id);
        //model.addAttribute("linkList",repoOlap.findAllByOlapIndexerOrderByIndexerAsc(tempOlapIndex));
        List<olap> tempLinkList= repoOlap.findAllByOlapIndexerAndD1IsInOrderByIndexerAsc(
                tempOlapIndex,
                repoOlapUserTown.findAllByUser(user).stream().map(olapUserTown::getTitle).collect(Collectors.toList())
            );
            olap tempOlap5=repoOlap.findByOlapIndexerAndIndexer(tempOlapIndex, 5);
            
            if(tempOlap5.getD1()==null || tempOlap5.getD1().startsWith("جمع")==false)
                tempLinkList .add(0,tempOlap5);
            tempLinkList .add(0,repoOlap.findByOlapIndexerAndIndexer(tempOlapIndex, 4));
            tempLinkList .add(0,repoOlap.findByOlapIndexerAndIndexer(tempOlapIndex, 3));
        model.addAttribute("linkList",tempLinkList);
        model.addAttribute("olapIndex",tempOlapIndex);

        writeExcel excel=new writeExcel();
        
        model.addAttribute("fileID",excel.write(tempOlapIndex,tempLinkList,uploadExcel));
        log("## view Report ##","User : "+getCurrentUserName()+" Report : "+tempOlapIndex.getTitle()+" -> "+tempOlapIndex.getId());
        return "admin/viewExcel";
    }
    @GetMapping("/admin/addHierarchy")
    public String addHierarchy(Long id,Model model){
        model=isAuth(model);
        //find current user and send it to Page
        model=getLoggedUser(model);
        hierarchy temp=new hierarchy();
        temp.setId(id);
        model.addAttribute("obj",temp);
        //find the All GroupManager and send it to <option>
        model.addAttribute("linker",repoLinker.findById(id));
        return "admin/addHierarchy";
    }
    @RequestMapping(value="/admin/addHierarchy",method=RequestMethod.POST)
    public String addHierarchy(@ModelAttribute @Valid ara.models.hierarchy model,BindingResult bindingResult,Model modeling){
        modeling=isAuth(modeling);
        if(bindingResult.hasErrors())
        {
            log("## Error Input ##",bindingResult.getAllErrors().get(0)+"");
            return "admin/addHierarchy";
        }
        else
        {
            Long linkerId=model.getId();
            model.setLinker(repoLinker.findLinkerById((long)linkerId));
            model.setId(null);
            repoHierarchy.save(model);
            return("redirect:/admin/hierarchyManagement?id="+linkerId);
        }

    }
    @GetMapping("/admin/addLinker")
    public String addLinker(Model model){
        model=isAuth(model);
        //find current user and send it to Page
        model=getLoggedUser(model);
        model.addAttribute("obj",new linker());
        //find the All GroupManager and send it to <option>
        model.addAttribute("workGroups",repoWorkGroup.findAll());
        return "admin/addLinker";
    }

    @RequestMapping(value="/admin/addLinker",method=RequestMethod.POST)
    public String addLinker(@ModelAttribute @Valid ara.models.linker model,BindingResult bindingResult,Model modeling){
        modeling=isAuth(modeling);
        if(bindingResult.hasErrors())
        {
            log("## Error Input ##",bindingResult.getAllErrors().get(0)+"");
            return "admin/addLinker";
        }
        else
        {
            model.setWorkGroups(repoWorkGroup.findById((long)model.getId()));
            model.setId(null);
            repoLinker.save(model);
            return("redirect:/admin/linkerManagement");
        }

    }

    @GetMapping("/admin/addWorkGroup")
    public String addWorkGroup(Model model){
        model=isAuth(model);
        //find current user and send it to Page
        model=getLoggedUser(model);
        model.addAttribute("obj",new workGroup());
        //find the All GroupManager and send it to <option>
        model.addAttribute("managers",repoUser.findAllByRoles(repoRole.findByName("ROLE_MANAGER")));
        return "admin/addWorkGroup";
    }

    @RequestMapping(value="/admin/addWorkGroup",method=RequestMethod.POST)
    public String addWorkGroup(@ModelAttribute @Valid workGroup model,BindingResult bindingResult,Model modeling){
        modeling=isAuth(modeling);
        if(bindingResult.hasErrors())
        {
            log("## Error Input ##",bindingResult.getAllErrors().get(0)+"");
            return "admin/addUser";
        }
        else
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User tempuserrepoUser=repoUser.findUserByUsername(authentication.getName());
            User Owner=repoUser.findUserById(model.getId());
            model.setOwner(Owner);
            model.setId(null);
            //model.setAnyOneGroup(false);
            //model.setOwner(tempuserrepoUser);
            ArrayList<User> tempList=new ArrayList<User>();
            tempList.add(tempuserrepoUser);
            tempList.add(Owner);
            model.setMembers(tempList);
            model.setEnabled(true);
            repoWorkGroup.save(model);
            return("redirect:/admin/groupManagement?ownerId=-1");
        }

    }


    private void log(String  head,String log){
        Calendar cal = Calendar.getInstance();
        System.out.println(head + " #"+cal.getTime()+" : "+log);
        return;
    }

}
