package ara.repository;

import ara.models.User;
import ara.models.document;
import ara.models.excelFile;
import ara.models.hierarchy;
import ara.models.workGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface  excelRepository extends PagingAndSortingRepository<excelFile,Long> {
   


}
