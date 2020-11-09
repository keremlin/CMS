package ara.repository;

import ara.models.Role;
import ara.models.User;
import ara.models.workGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Long> {
    List<User> findAll();
    int removeUserById(long id);
    User findUserById(long id);
    User findUserByUsername(String username);
    User findByUsername(String userName);
    List<User> findAllByWorkGroups(workGroup wg);
    List<User> findAllByIdNotIn(List<Long> list);
    List<User> findAllByRoles(Role role);
    Page<User> findAll(Pageable pageable);
    Page<User> findUserByWorkGroups(Pageable pageable,workGroup workGroup);

}
