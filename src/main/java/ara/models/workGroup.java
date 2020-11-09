package ara.models;

import lombok.Data;
import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
public class workGroup {
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private boolean enabled;


    private boolean anyOneGroup;

    @Column(columnDefinition = "NUMBER(1,0) default 0")
    private boolean ftpGroup;

    @ManyToOne(targetEntity = ara.models.User.class)
    @JoinColumn(name = "userOwner_id", referencedColumnName = "id")
    private User owner;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name="workGroupId",referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="UsersId",referencedColumnName="id")
    )
    private Collection<User> members;

    @ManyToMany(cascade = {CascadeType.REMOVE},mappedBy = "workGroups")
    private Collection<document> documents;
}
