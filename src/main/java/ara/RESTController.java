package ara;

import ara.models.*;
import ara.repository.LinkerRepository;
import ara.repository.RoleRepository;
import ara.repository.UserRepository;
import ara.repository.documentRepository;
import ara.repository.hierarchyRepository;
import ara.repository.documentCommentRepository;
import ara.repository.workgroupRepository;

import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
public class RESTController {

    @Autowired
    private documentRepository repodocument;
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private workgroupRepository repoWorkgroup;
    @Autowired
    private RoleRepository repoRole;
    @Autowired
    private documentCommentRepository repoDocumentCommentRepository;
    @Autowired
    private hierarchyRepository repoHierarchy;
    @Autowired 
    private LinkerRepository repoLinker;
    @Autowired
    private ara.repository.olapIndexRepository repoOlapIndex;
    @Autowired
    private ara.repository.olapRepository repoOlap;
    @Autowired
    private ara.repository.olapUserTownRepository repoOlapUserTown;

    @RequestMapping(method= RequestMethod.POST,value="/REST/editor/disableComment")
    public vmStatus  disableComment(int commentId) {
        return disableOrEnableComment(commentId,false);
    }
        @RequestMapping(method= RequestMethod.POST,value="/REST/editor/enableComment")
    public vmStatus  enableComment(int commentId) {
        return disableOrEnableComment(commentId,true);
    }
    //disable or enable comment
    public vmStatus  disableOrEnableComment(int commentId,boolean item) {
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());
        log("##    remove comment  ##"," User : "+getCurrentUserName()+ "id :"+commentId );
        //find target document
        var temp=repoDocumentCommentRepository.findDocumentCommentById(commentId);
        //check if user has permission to save a comment
        if(currentUser!=null && temp!=null &&
                (currentUser.getUsername().startsWith("Admin")||
                        temp.getUser().getUsername()==currentUser.getUsername() ||
                        temp.getDocument().getWorkGroups().iterator().next().getOwner().getUsername()==currentUser.getUsername()))
                        {
                            try {
                                
                                temp.setEnabled(item);
                                repoDocumentCommentRepository.save(temp);
                                return new vmStatus(){{
                                    setCode(200);
                                    setId(temp.getId());
                                    setMessage("با موفقیت غیر فعال گردید");
                                    setStatus("done");
                                    setDateTime(new Date());
                                }};
                            }
                            catch (Exception ex){
                                return new vmStatus(){{
                                   
                                    setCode(500);
                                    setId(temp.getId());
                                    setMessage("خطا در ذخیره سازی");
                                    setStatus(ex.getMessage());
                                    setDateTime(new Date());
                                }};
                            }
                        }
       
        return new vmStatus(){{
            setCode(400);
            setId(temp.getId());
            setMessage("شما با این نام کاربری مجاز به این عمل نمی باشید");
            setStatus("login Err");
            setDateTime(new Date());
        }};
    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/editor/saveComment")
    public vmStatus  saveComment(int documentId,String comment){
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());
        //find target document
        document targetDocument=repodocument.findDocumentById(documentId);
        //check if user has permission to save a comment
        if(currentUser!=null && targetDocument!=null && targetDocument.isAllowComment() &&
                (currentUser.getUsername().startsWith("Admin")||
                targetDocument.getWorkGroups().iterator().next().getMembers().contains(currentUser))) {

            
            //create new comment of db
            ara.models.documentComment item = new documentComment();
            item.setDocument(targetDocument);
            item.setCreateDate(new Date());
            item.setEnabled(false);
            item.setMessage(comment);
            item.setUser(currentUser);

            // persist item
            ara.models.documentComment savedComment = repoDocumentCommentRepository.save(item);
            //return a message to page
            return new vmStatus() {{
                setCode(200);
                setId(savedComment.getId());
                setMessage("نظر با موفقیت ثبت گردید");
                setStatus(currentUser.getFirstName()+" "+currentUser.getLastName());
            }};
        }
        return new vmStatus() {{
            setCode(500);
            setId(-1);
            setMessage("خطا شما عضو این گروه نیستید - وارد شوید");
            setStatus("error");
        }};
    }

    @RequestMapping(method= RequestMethod.POST,value="/REST/editor/save")
    public vmStatus  save(@RequestBody document doc1){
        //find the hierarchy if any exists.
        if(doc1.getSecurityLevel()>0)
            doc1.setHierarchy(repoHierarchy.findHierarchyById((long)doc1.getSecurityLevel()));
        doc1.setSecurityLevel(0);
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());
        // create new date for changes
        Date date = new Date();
        //extract workGroupID from content that sent by ajax
        String workGroupId=doc1.getDescription().substring(doc1.getDescription().indexOf("###")+3);
        //try to find the workGroup that specified
        workGroup tempWorkGroup=repoWorkgroup.findById(Integer.parseInt(workGroupId));
        //define owner of document
        User Owner=null;
        // if NOT new document
        if(Integer.parseInt(workGroupId)==0) {
            document temp;
            ArrayList<workGroup> temp2=new ArrayList<>();
            temp = repodocument.findDocumentById(doc1.getId());
            temp2.addAll(temp.getWorkGroups());
            doc1.setWorkGroups(temp2);
            Owner=temp.getOwner();
        }
        else//if a new document
        {
            doc1.setWorkGroups(new ArrayList<workGroup>(){
                {
                    add(
                            tempWorkGroup);
                }});
            if(doc1.getReviewStep()==0)//if a public document
                Owner=currentUser;
            else {// if a direct document

                if(currentUser.getRoles().iterator().next().getName().startsWith("ROLE_MANAGER") ||
                        currentUser.getRoles().iterator().next().getName().startsWith("ROLE_ADMIN"))
                    Owner = repoUser.findUserById(doc1.getReviewStep());
                else
                    Owner=currentUser;

                doc1.setDirect(true);
            }
        }

        doc1.setEditDate(date);
        doc1.setCreateDate(date);
        doc1.setDestroyDate(date);
        //check for FTP users posts & check if document is direct
        if(currentUser.getRoles().contains(repoRole.findByName("ROLE_FTP")) || doc1.getReviewStep()>0)
            doc1.setEnabled(false);
        else
            doc1.setEnabled(true);

        doc1.setOwner(Owner);
        doc1.setDescription(doc1.getDescription().substring(0,doc1.getDescription().indexOf("###")));
        doc1.setCssClass(doc1.getCssClass());
        doc1.setReviewStep(0);
        //ArrayList<workGroup> wg =new ArrayList<>(doc1.getOwner().getWorkGroups());
        //doc1.setWorkGroups();
        document documentSaved=repodocument.save(doc1);
        log("## REST Save Document ##"," User : "+getCurrentUserName()+ " State : Success Document id : " + doc1.getId());
        return new vmStatus(){{
            setCode(200);
            setId(documentSaved.getId().intValue());
            setMessage("با موفقیت ثبت گردید");
            setStatus("Saved");
        }};

    }

    @RequestMapping(value="/REST/editor/load")
    public document load(int documentId){
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());

        //check if user is logged in then use correct userName
        String userName="Anonymous";
        if (currentUser!=null)
            userName=currentUser.getUsername();
        //find document object
        document temp=repodocument.findDocumentById(documentId);
        //load the document and send it via vm
        if(temp!=null) {
            log("## REST load Document ##","User : "+getCurrentUserName()+ "Document id : " + documentId);
            return new vmFullDocument(userName,
                    temp.getWorkGroups().iterator().next().getOwner().getUsername()) {{
                setCreateDate(temp.getCreateDate());
                setDescription(temp.getDescription());
                setTitle(temp.getTitle());
                setEditDate(temp.getEditDate());
                setDocumentContent(temp.getDocumentContent());
                setId(temp.getId());
                setAllowComment(temp.isAllowComment());
                setWorkGroup(temp.getWorkGroups().iterator().next().getName());
                setDocumentOwner(temp.getOwner().getLastName());
                if(currentUser!=null && (currentUser.getUsername().startsWith("Admin")||
                        temp.getWorkGroups().iterator().next().getOwner().getUsername()==currentUser.getUsername())
                ) {
                    setComments(repoDocumentCommentRepository.findByDocumentOrderByCreateDateDesc(temp));
                }else if(currentUser==null){}
                else {
                    setComments(repoDocumentCommentRepository.findByDocumentAndEnabledOrderByCreateDateDesc(temp, true));
                }
            }};
        }
        else {
            
            return null;

        }

    }
    @Value("${ara.documentPagging}")
    private int documentPagging;
    @RequestMapping(value = "/REST/admin/getNextPage",method = RequestMethod.POST)
    public @ResponseBody List<vmDocuments> getNextPage(int pageNumber, int workGroupId,int userId){
        //get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());
        Page<document> pagesOfDocuments=null;
        Pageable firstPageWithTwoElements = PageRequest.of(pageNumber, documentPagging);

        if(authentication.getName().startsWith("Admin")==true) {
            if(userId>0)
                pagesOfDocuments=repodocument.findAllByOwnerOrderByEditDateDesc(repoUser.findUserById(userId),firstPageWithTwoElements);
            else if(workGroupId==-1)
                pagesOfDocuments=repodocument.findAllByOrderByEditDateDesc(firstPageWithTwoElements);
            else
                pagesOfDocuments=repodocument.findAllByWorkGroupsOrderByEditDateDesc(
                        repoWorkgroup.findById(workGroupId),firstPageWithTwoElements);
        }
        else if(currentUser.getRoles().contains(repoRole.findByName("ROLE_FTP"))){
            pagesOfDocuments=repodocument.findAllByOwnerOrderByEditDateDesc(
                    repoUser.findUserByUsername(authentication.getName()),firstPageWithTwoElements);
        }
        else if(currentUser.getRoles().contains(repoRole.findByName("ROLE_MANAGER"))){

            if(userId<0)
            pagesOfDocuments=repodocument.findAllByWorkGroupsIsInOrderByEditDateDesc(
                    repoWorkgroup.findByOwner(repoUser.findUserByUsername(authentication.getName())),
                    firstPageWithTwoElements);
            else
                pagesOfDocuments=repodocument.findAllByOwnerAndWorkGroupsIsInOrderByEditDateDesc(
                        repoUser.findUserById(userId),
                        repoWorkgroup.findByOwner(repoUser.findUserByUsername(authentication.getName())),
                        firstPageWithTwoElements);
        }

        List<document> temp=pagesOfDocuments.getContent();

        return vmDocuments.createList(temp,pageNumber,pagesOfDocuments.getTotalPages());
    }
    @Value("${ara.userPagging}")
    private int userPagging;

    @RequestMapping(value = "/REST/admin/selectHierarchy",method = RequestMethod.POST)
    public @ResponseBody List<valueAndKey> selectHierarchy(int id){
        
        List<valueAndKey> temp = new ArrayList<>();
        temp.add(new valueAndKey()
            {{
                setValue("انتخاب زیرشاخه");
                setKey(0);
            }}
        );

        for(hierarchy item : repoHierarchy.findHierarchyByLinker(repoLinker.findByWorkGroups(repoWorkgroup.findById(id))))
        {
            temp.add(new valueAndKey() {{
                setValue(item.getTitle());
                setKey(item.getId());
            }});      
        }
        
        return temp;
    }
        
    @RequestMapping(value = "/REST/admin/selectPerson",method = RequestMethod.POST)
    public @ResponseBody List<valueAndKey> selectPerson(int id){
        //get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser=repoUser.findUserByUsername(authentication.getName());
        String currentRole=currentUser.getRoles().iterator().next().getName();
        List<valueAndKey> temp = new ArrayList<>();

        temp.add(new valueAndKey()
                 {{
                     setValue("دریافت کننده سند");
                     setKey(0);
                 }}
        );

        if(currentRole.startsWith("ROLE_MANAGER") ||currentRole.startsWith("ROLE_ADMIN")) {//if user is admin or manager

            for (User item : repoWorkgroup.findById(id).getMembers()) {
                temp.add(new valueAndKey() {{
                    setValue(item.getLastName() + " " + item.getFirstName());
                    setKey(item.getId());
                }});
            }

        }else{//if user is public
            User item= repoWorkgroup.findById(id).getOwner();//find only group owner

            temp.add(new valueAndKey()
                {{
                    setValue(item.getLastName() + " " + item.getFirstName());
                    setKey(item.getId());
                }}
                );

        }
        return temp;
    }
    @RequestMapping(value = "/REST/admin/getUserNextPage",method = RequestMethod.POST)
    public @ResponseBody List<vmUser> getUserNextPage(int pageNumber,int workGroupId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<User> pagesOfDocuments;
        Pageable firstPageWithTwoElements = PageRequest.of(pageNumber, userPagging);

        if(authentication.getName().startsWith("Admin")==true) {

            //check if workGroup filter is sent
            if(workGroupId==-1)
                pagesOfDocuments=repoUser.findAll(firstPageWithTwoElements);
            else
                pagesOfDocuments=repoUser.findUserByWorkGroups(
                        firstPageWithTwoElements,repoWorkgroup.findById(workGroupId));

            List<User> temp=pagesOfDocuments.getContent();
            return vmUser.createList(temp,pageNumber,pagesOfDocuments.getTotalPages());
        }
        return null;
    }
    @RequestMapping(value = "/REST/admin/olapGetNotIncludedCities",method = RequestMethod.POST)
    public @ResponseBody List<String> olapGetNotIncludedCities(long person){
        List<olap> towns=repoOlap.findAll();
        List<String> newTowns = new ArrayList(); //towns.stream() 
                                //      .distinct() 
                                //      .collect(Collectors.toList());
        for(olap item:towns)
            if(item.getD1()!=null && item.getD1()!="")
                newTowns.add((item.getD1()).trim());

        newTowns=newTowns.stream().distinct().collect(Collectors.toList());
        return newTowns;
    }
    @RequestMapping(value = "/REST/admin/olapGetIncludedCities",method = RequestMethod.POST)
    public @ResponseBody List<String> olapGetIncludedCities(long person){
        //get user town included
        List<olapUserTown> newTowns = repoOlapUserTown.findAllByUser(
            repoUser.findUserById(person)
            );
            List<String> newTownsTemp = new ArrayList(); //towns.stream() 
            //      .distinct() 
            //      .collect(Collectors.toList());
            for(olapUserTown item:newTowns)
                if(item.getTitle()!=null && item.getTitle()!="")
                    newTownsTemp.add((item.getTitle()).trim());
            
        return newTownsTemp;
    }
    @RequestMapping(value = "/REST/admin/setCityToPerson",method = RequestMethod.POST)
    public @ResponseBody vmStatus setCityToPerson(long person,String city){
        //init status 
        vmStatus status=new vmStatus();
        //get user 
        User user=repoUser.findUserById(person);
        // find user's towns
        List<olapUserTown> newTowns = repoOlapUserTown.findAllByUserAndTitle(
                user,
                city
            );
        //if user dont have the town
        if(newTowns.size()==0){
            //save the new town to the user
            olapUserTown temp=new olapUserTown();
            temp.setTitle(city);
            temp.setUser(user);
            repoOlapUserTown.save(temp);
            status.setCode(1);
        }
        else{
            // city is available
            status.setCode(-1);
        }
        return status;
    }
    @RequestMapping(value = "/REST/admin/deleteCityToPerson",method = RequestMethod.POST)
    public @ResponseBody vmStatus deleteCityToPerson(long person,String city){
        
        //init status 
        vmStatus status=new vmStatus();
        //get user 
        User user=repoUser.findUserById(person);
        // find user's towns
        List<olapUserTown> newTowns = repoOlapUserTown.findAllByUserAndTitle(
                user,
                city
            );
        //try to delete the record
        try{
            status.setCode(200);       
            repoOlapUserTown.deleteAll(newTowns);
        }catch(Exception ex){status.setCode(100);}
            return status;
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
