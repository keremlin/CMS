package ara.repository;

import ara.models.User;
import ara.models.olapUserTown;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface olapUserTownRepository extends CrudRepository<olapUserTown,Long> {
    
    List<olapUserTown> findAllByUser(User user);
    List<olapUserTown> findAllByUserAndTitle(User user,String title);
    
    
}
