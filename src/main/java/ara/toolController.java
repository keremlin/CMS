package ara;

import ara.models.document;
import ara.models.documentFile;
import ara.models.vmStatus;
import ara.repository.documentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ara.repository.documentFileRepository;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

@RestController
public class toolController implements WebMvcConfigurer {

    @Autowired
    private documentRepository repoDocument;
    @Autowired
    private documentFileRepository repoFileDocument ;
    // check how many Group a user have
    @RequestMapping(method= RequestMethod.POST,value="/REST/admin/enableCommentForDocument")
    public vmStatus enableCommentForDocument(long id ){
        int numberOfError=0;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        document doc=repoDocument.findDocumentById(id);
        log("## enabling commenting ##", "this document is enabled: "
                +authentication.getName().equals(
                        doc.getWorkGroups().iterator().next().getOwner().getUsername())+" id:"+ id);
        if(doc!=null &&
                (authentication.getName().startsWith("Admin")==true ||
                doc.getWorkGroups().iterator().next().getOwner().getUsername().equals(
                        authentication.getName())))
        {
            try {
                doc.setAllowComment(!doc.isAllowComment());
                repoDocument.save(doc);
            }
            catch (Exception ex){numberOfError++;}
        }
        else numberOfError++;

        if(numberOfError==0)
            return(new vmStatus(){{
                setCode(200);
                setMessage("کامنت غیر فعال شد");
                setStatus(doc.isAllowComment()+"");
                setId(Integer.valueOf(id+""));
                setDateTime(new Date());
            }});
        else
            return(new vmStatus(){{
                setCode(500);
                setMessage("خطا سند یافت نشد.");
                setStatus("not baned");
                setId(Integer.valueOf(id+""));
                setDateTime(new Date());
            }});
    }
    @RequestMapping(method= RequestMethod.POST,value="/REST/admin/banPost")
    public vmStatus banPost(int id){
        int numberOfError=0;
        document Ducument =repoDocument.findDocumentById(id);
        if( Ducument!=null && Ducument.isDirect()==false)//check if document is not a direct
        {
            try {
                Ducument.setEnabled(!Ducument.isEnabled());
                repoDocument.save(Ducument);
            }catch(Exception ex){numberOfError++;}
        }else{
            numberOfError++;
        }

        log("## ADMIN ##","The document is baned : " +id);

        if(numberOfError==0)
            return(new vmStatus(){{
                setCode(200);
                setMessage("فایل غیر فعال شد");
                setStatus(Ducument.isEnabled()+"");
                setId(Integer.valueOf(id+""));
                setDateTime(new Date());
            }});
        else
            return(new vmStatus(){{
                setCode(500);
                setMessage("خطا سند یافت نشد.");
                setStatus("not baned");
                setId(Integer.valueOf(id+""));
                setDateTime(new Date());
            }});
    }
    @Transactional
    @RequestMapping(method= RequestMethod.POST,value="/REST/admin/deletePosts")
    public vmStatus deletePosts(Long id) {

        //delete all attached physical files
        int numberOfError=0;
        ara.models.document  item =repoDocument.findDocumentById(id);
        for(documentFile doc : item.getDocumentFile()) {
            if(deleteFile(doc)==false) numberOfError++;
            log("## ADMIN ##","The Physical File is removed : "+doc.getName()+" id: "+doc.getId());
        }

        // if all files deleted properly delete document itself
        if(numberOfError==0){
            try {
                repoDocument.deleteById(id);
                log("## ADMIN ##", "The document is removed : " + id);
            }
            catch(Exception ex){// if document were not deleted
                numberOfError=1;
                log("##  error POST NOT REMOVED",item.getId()+"");
            }
        }
        else//else create error
            log("##  error POST files NOT REMOVED",item.getId()+"");


    if(numberOfError==0)
        return(new vmStatus(){{
            setCode(200);
            setMessage("سند و فایلها ی ضمیمه ی آن با موفقیت حذف شد");
            setStatus("deleted");
            setId(Integer.valueOf(id+""));
            setDateTime(new Date());
        }});
    else
        return(new vmStatus(){{
            setCode(500);
            setMessage("خطا سند یا فایلها ی ضمیمه ی آن  حذف نشد");
            setStatus("not deleted");
            setId(Integer.valueOf(id+""));
            setDateTime(new Date());
        }});
    }

    private boolean deleteFile(documentFile doc){

        try {
            File fileToDelete = new File(doc.getPath());
            fileToDelete.delete();
            repoFileDocument.delete(doc);
            log("## PHYSICAL FILE DELETED ##",doc.getPath());

        }
        catch(Exception ex){log("## DELETE PHYSICAL FILE FAILED ", doc.getPath());return false;}
        return true;
    }
    private void log(String  head,String log){
        Calendar cal = Calendar.getInstance();
        System.out.println(head + " #"+cal.getTime()+" : "+log);
        return;
    }
    //return current user object
    /*private User currentUser(){
        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repoUser.findUserByUsername(authentication.getName());
    }*/
    /*private boolean isCurrentUserManagerAndHigher(){
        String roleName=currentUser().getRoles().iterator().next().getName();

        if(roleName.startsWith("ROLE_ADMIN")||roleName.startsWith("ROLE_MANAGER"))
            return true;
        else
            return false;
    }*/
}
