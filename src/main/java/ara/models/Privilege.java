package ara.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
public class Privilege {
    public Privilege(){}
    public Privilege(String name){this.name=name;}
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
}
