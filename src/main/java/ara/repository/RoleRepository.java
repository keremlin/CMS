package ara.repository;

import ara.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role,Long> {
    ara.models.Role findByName(String name);
    List<Role> findAllByName(String name);
    List<Role> findAll();
    ara.models.Role findRoleById(Long id);
}
