package ara.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class vmFullDocument extends document  {
    private String workGroup;
    private String documentOwner;
    private String userName;
    private String groupOwner;

    public vmFullDocument(String username,String groupOwner){
        this.userName=username;
        this.groupOwner=groupOwner;
    }

    public void setComments(Collection<documentComment> comments){
        // get full name of user of comment to send with jason as eager loading.
        this.comments=new ArrayList<>();
        for(documentComment item : comments){
            this.comments.add(new vmDocumentComment(item,this.userName,this.groupOwner));
        }
    }
    private Collection<vmDocumentComment> comments;
}
