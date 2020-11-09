package ara.models;

import lombok.Data;

@Data
public class vmDocumentComment extends documentComment {

    public vmDocumentComment(documentComment item,String userName,String groupOwner){
        // get full name of user in order to eager loading

        this.userFullName=item.getUser().getFirstName() +" "+item.getUser().getLastName();
        this.setUser(item.getUser());
        this.setCreateDate(item.getCreateDate());
        this.setDocument(item.getDocument());
        this.setEnabled(item.isEnabled());
        this.setMessage(item.getMessage());
        this.setId(item.getId());
        if(item.getUser().getUsername()==userName ||
                userName.startsWith("Admin")||
               userName==groupOwner
        )
        {this.setOwner(true);}
    }
    private String userFullName;
    private boolean owner=false;
}
