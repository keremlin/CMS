package ara.models;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@RequiredArgsConstructor
@Table(name="users")
public class User {
    public User(String username,String firstName,String lastName,String email,String password,boolean tokenExpired,boolean enabled){
        this.username=username;this.firstName=firstName;this.lastName=lastName;this.email=email;this.password=password;this.tokenExpired=tokenExpired;
        this.enabled=enabled;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min=5 ,message="نام کاربری باید بیش از 5 کاراکتر باشد")
    @NotNull
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;
    private boolean tokenExpired;
    private String lastIpAddress;
    private Long tries;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Temporal(TemporalType.TIMESTAMP)
    private Date previousLogin;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;


    @ManyToMany(mappedBy = "members")
    private Collection<workGroup> workGroups;

    @OneToMany(cascade={CascadeType.REMOVE},mappedBy = "owner")
    private Collection <document> documents;

    @OneToMany(mappedBy = "owner")
    private Collection<workGroup> ownWorkGroup;
}



