package ara.models;

import lombok.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class vmUser extends User {




        public vmUser(Long id,String username,String firstName,String lastName,
                      String email,String password,String role,boolean tokenExpired,
                      boolean enabled,int pageNumber,int totalPages){
            super(username,firstName,lastName,email,password,tokenExpired,enabled);
            this.roleName=role;this.vmId=id;this.pageNumber=pageNumber;this.totalPages=totalPages;
        }
        private String roleName;
        private Long vmId;
        private int pageNumber;
        private int totalPages;
        public static List<vmUser> createList(List<User> list, int pageNumbers,int totalPage) {


            List<vmUser> vmuser = new ArrayList<>();

            for (User item : list) {
                Collection<Role> role = item.getRoles();
                List<Role> newlist;

                if (role instanceof List)
                    newlist = (List) role;
                else
                    newlist = new ArrayList<Role>(role);

                ara.models.Role newrole = newlist.get(0);

                vmuser.add(new vmUser(item.getId(), item.getUsername(), item.getFirstName(), item.getLastName(), item.getEmail()
                        , item.getPassword(), newrole.getName(), item.isTokenExpired(), item.isEnabled(), pageNumbers,
                        totalPage));
            }
            return vmuser;
        }
}
