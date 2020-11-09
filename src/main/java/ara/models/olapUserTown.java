package ara.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@NoArgsConstructor
public class olapUserTown { 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //
    //private Collection<olap> olap;
    
    @ManyToOne
    private User user;
    
    private String title;
    
    

    
}