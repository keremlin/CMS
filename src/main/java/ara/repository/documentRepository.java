package ara.repository;

import ara.models.User;
import ara.models.document;
import ara.models.hierarchy;
import ara.models.workGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface  documentRepository extends PagingAndSortingRepository<document,Long> {
   document findDocumentById(long id);
   List<document> findAllByWorkGroupsIsIn(Collection<workGroup> WorkGroup);
   List<document> findAllByWorkGroupsIsInAndEnabled(Collection<workGroup> WorkGroup,boolean isEnabled);
   List<document> findAllByWorkGroupsIsInAndEnabledOrderByEditDateDesc(Collection<workGroup> WorkGroup,boolean isEnabled);
   Page<document> findAllByWorkGroupsIsIn(Collection<workGroup> WorkGroup,Pageable pageable);
   Page<document> findAllByWorkGroupsIsInOrderByEditDateDesc(Collection<workGroup> WorkGroup,Pageable pageable);
   Page<document> findAllByOwnerAndWorkGroupsIsIn(User Owner,Collection<workGroup> WorkGroup,Pageable pageable);
   Page<document> findAllByOwnerAndWorkGroupsIsInOrderByEditDateDesc(User Owner,Collection<workGroup> WorkGroup,Pageable pageable);
   Page<document> findAllByWorkGroupsIsInAndEnabled(Collection<workGroup> WorkGroup,Pageable pageable,boolean isEnabled);
   Page<document> findAllByWorkGroupsIsInAndEnabledOrderByEditDateAsc(Collection<workGroup> WorkGroup,Pageable pageable,boolean isEnabled);
   Page<document> findAllByWorkGroupsIsInAndEnabledOrderByEditDateDesc(Collection<workGroup> WorkGroup,Pageable pageable,boolean isEnabled);

   List<document> findAllByWorkGroups(workGroup WorkGroup);
   List<document> findAllByWorkGroupsOrderByEditDateDesc(workGroup WorkGroup);

   List<document> findAllByWorkGroupsAndEnabled(workGroup WorkGroup,boolean isEnabled);
   List<document> findAllByWorkGroupsAndEnabledOrderByEditDateDesc(workGroup WorkGroup,boolean isEnabled);

   Page<document> findAllByWorkGroups(workGroup WorkGroup, Pageable pageable);
   Page<document> findAllByWorkGroupsOrderByEditDateDesc(workGroup WorkGroup, Pageable pageable);
   Page<document> findAllByWorkGroupsAndEnabled(workGroup WorkGroup, Pageable pageable,boolean isEnabled);
   Page<document> findAllByWorkGroupsAndEnabledOrderByEditDateDesc(workGroup WorkGroup, Pageable pageable,boolean isEnabled);

   Page<document> findAllByOwner(User Owner, Pageable pageable);
   Page<document> findAllByOwnerOrderByEditDateDesc(User Owner, Pageable pageable);
   Page<document> findAll(Pageable pageable);
   Page<document> findAllByOrderByEditDateDesc(Pageable pageable);
   List<document> findAll();

   int countAllByOwner(User user);
   List<document> findAllByOwner(User Owner);
   List<document> findAllByOwnerOrderByEditDateDesc(User Owner);
   
   List<document> findAllByHierarchyOrderByEditDateDesc(hierarchy Hierarchy);

   int countAllByWorkGroupsIsInAndEnabled(List<workGroup> workGroup,boolean enabled);
   List<document> findAllByWorkGroupsIsInAndEnabled(List<workGroup> workGroup,boolean enabled);
   List<document> findAllByWorkGroupsIsInAndEnabledOrderByEditDateDesc(List<workGroup> workGroup,boolean enabled);

   int countAllByOwnerAndDirectAndCreateDateGreaterThan(User owner,boolean direct,java.util.Date time);
   List<document> findAllByOwnerAndDirectAndCreateDateGreaterThan(User owner,boolean direct,java.util.Date time);
   List<document> findAllByOwnerAndDirectAndCreateDateGreaterThanOrderByEditDateDesc(User owner,boolean direct,java.util.Date time);

   int countAllByWorkGroupsIsInAndDirectAndCreateDateGreaterThan(Collection<workGroup> workGroup,boolean direct,java.util.Date time);
   List<document> findAllByWorkGroupsIsInAndDirectAndCreateDateGreaterThan(Collection<workGroup> workGroups,boolean direct,java.util.Date time);
   List<document> findAllByWorkGroupsIsInAndDirectAndCreateDateGreaterThanOrderByEditDateDesc(Collection<workGroup> workGroups,boolean direct,java.util.Date time);

   int countAllByWorkGroupsIsInAndEnabledAndCreateDateGreaterThan(List<workGroup> workGroup, boolean enabled, java.util.Date time);
   List<document> findAllByWorkGroupsIsInAndEnabledAndCreateDateGreaterThan(Collection<workGroup> workGroup, boolean enabled, java.util.Date time);
   List<document> findAllByWorkGroupsIsInAndEnabledAndCreateDateGreaterThanOrderByEditDateDesc(Collection<workGroup> workGroup, boolean enabled, java.util.Date time);


}
