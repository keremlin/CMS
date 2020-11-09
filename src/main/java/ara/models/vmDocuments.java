package ara.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class vmDocuments {

    private Long id;
    private String title;
    private String description;
    private Date createDate;
    private Date editDate;
    private String documentContent;
    private String owner;
    private String workGroups;
    private String status;
    private String allowComment;
    private int pageNumber;
    private int allPages;

    public static List<vmDocuments> createList(List<document> tempDocumentList,int pageNumber,int allPages){
        List<vmDocuments> tempDocuments=new ArrayList<>();
        for(document doc : tempDocumentList){
            String tempWorkGroupString="";
            // get name of all workGroup that this document is belong to
            for(workGroup wg:doc.getWorkGroups())
                tempWorkGroupString+=wg.getName()+"-";
            final String temppppp=tempWorkGroupString;
            // check document status
            String statuses="";
            if(doc.isDirect())
                statuses="سند مستقیم";
            else if(doc.isEnabled())
                statuses="فعال";
            else if(doc.isEnabled()==false)
                statuses="غیر فعال";
            final String x=statuses;
            // add new vm of current document
            tempDocuments.add(new vmDocuments(){{
                setCreateDate(doc.getCreateDate());
                setEditDate(doc.getEditDate());
                setId(doc.getId());
                setDescription(doc.getDescription());
                setTitle(doc.getTitle());
                setOwner(doc.getOwner().getUsername());
                setWorkGroups(temppppp);
                setStatus(x);
                setAllowComment(((doc.isAllowComment()==true) ?"فعال":"غیرفعال"));
                setPageNumber(pageNumber);
                setAllPages(allPages);
            }});
        }
        return tempDocuments;

    }
}


