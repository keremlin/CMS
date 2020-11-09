package ara.repository;

import ara.models.Privilege;
import org.springframework.data.repository.CrudRepository;


public interface PrivilegeRepository extends CrudRepository <Privilege,Long> {
    Privilege findByName(String name);
}
