package ara.models;

import lombok.Data;

@Data
public class vmWorkGroup {
    public vmWorkGroup(long id,String name,String description,String owner,String members,String enabled,String anyOneGroup){
        this.description=description;this.name=name;this.members=members;this.owner=owner;this.id=id;
        this.enabled=enabled;this.anyOneGroup=anyOneGroup;
    }
    private String name;
    private Long id;
    private String description;
    private String owner;
    private String members;
    private String enabled;
    private String anyOneGroup;

}
