package ara.repository;

import ara.models.User;
import ara.models.workGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface workgroupRepository extends CrudRepository <workGroup,Long> {
    ArrayList<workGroup> findAll();
    workGroup findByName(String name);
    int removeById(long id);
    workGroup findById(long id);
    // check if this user belong to this group
    List<workGroup> findByIdAndMembers(long id,User user);
    // chech if this user is owner of this group
    workGroup findByIdAndOwner(long id,User owner);
    List<workGroup> findAllByIdNotIn(List<Long> list);
    List<workGroup> findAllByMembersContains(String id);
    ArrayList<workGroup> findByOwner(User user);
    ArrayList<workGroup> findByMembersAndFtpGroup(User user,boolean ftpEnabled);
    ArrayList<workGroup> findByMembers(User user);
    int countAllByMembersContains(User member);
    

}
