package ara;

import ara.models.*;
import ara.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
public class InitialDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;





    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private UserRepository userRepository;
    //@Autowired
    //private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private workgroupRepository workGrouprepo;
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_MANAGER",adminPrivileges);
        createRoleIfNotFound("ROLE_FTP",adminPrivileges);


        /*
        Role userRole= roleRepository.findByName("ROLE_USER");
        User user2 = new User();
        user2.setFirstName("محمد حسین");
        user2.setLastName("احمدی");
        user2.setPassword(passwordEncoder.encode("123123"));
        user2.setEmail("test@ahmadi.com");
        user2.setUsername("ahmadi");
        user2.setRoles(Arrays.asList(userRole));
        user2.setEnabled(true);
        userRepository.save(user2);*/

        /*
        Role ostanRole= roleRepository.findByName("ROLE_MANAGER");
        User user3 = new User();
        user3.setFirstName("علی");
        user3.setLastName("علوی");
        user3.setPassword(passwordEncoder.encode("123123"));
        user3.setEmail("test@alavi.com");
        user3.setUsername("alavi");
        user3.setRoles(Arrays.asList(ostanRole));
        user3.setEnabled(true);
        userRepository.save(user3);
        */

        //add workgroup
        /*workGroup wg=new workGroup();
        wg.setName("استان تهران");
        wg.setDescription("گروه کاربران استان تهران");
        wg.setOwner(user);
        ArrayList<User> tempuserlist=new ArrayList<>();
        tempuserlist.add(user3);
        tempuserlist.add(user2);
        tempuserlist.add(user);
        wg.setMembers(tempuserlist);
        wg.setEnabled(true);
        workGrouprepo.save(wg);

        //add defualt workGroups
        workGroup wg2=new workGroup();
        wg2.setName("عمومی");
        wg2.setDescription("گروه همه اعضا");
        wg2.setOwner(user);
        ArrayList<User> tempuserlist2=new ArrayList<>();
        wg2.setMembers(tempuserlist2);
        wg2.setEnabled(true);
        workGrouprepo.save(wg2);*/
        createPublicWorkGroupIfNotFound("عمومی","گروه عمومی");
        createPublicWorkGroupIfNotFound("اسکایپ","اسکایپ");
        createPublicWorkGroupIfNotFound("اسکایپ-دانلود","اسکایپ-دانلود");
        createPublicWorkGroupIfNotFound("سرویس","اطلاع رسانی سرویس ها");
        //add document
        



        alreadySetup = true;
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
    @Transactional
    public User createAdminIfNotFound(){
        try{
            User userCheck=userRepository.findUserByUsername("Admin");
            if(userCheck==null){

                Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                User user = new User();
                user.setFirstName("ادمین");
                user.setLastName("ادمین");
                user.setPassword(passwordEncoder.encode("123123"));
                user.setEmail("admin@admin.com");
                user.setUsername("Admin");
                user.setRoles(Arrays.asList(adminRole));
                user.setEnabled(true);
                userRepository.save(user);
                return user;
            }
        }
        catch(Exception ex){
            return null;
        }
       return null;
    }
    @Transactional
    public workGroup createPublicWorkGroupIfNotFound(String name,String description){
        workGroup wg2=new workGroup();
        User user=userRepository.findUserByUsername("Admin");
        try{
            if(workGrouprepo.findByName(name)==null){

            wg2.setName(name);
            wg2.setDescription(description);
            wg2.setOwner(user);
            ArrayList<User> tempUserList2=new ArrayList<>();
            tempUserList2.add(user);
            wg2.setMembers(tempUserList2);
            wg2.setEnabled(true);
            wg2.setAnyOneGroup(true);


                workGrouprepo.save(wg2);


            }
        }
        catch(Exception ex){
            wg2=null;
        }
        return wg2;
    }
}
