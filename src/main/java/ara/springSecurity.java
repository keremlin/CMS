package ara;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration

@EnableWebSecurity
public class springSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/admin/editor/showPost*").permitAll()
                .antMatchers("/admin/fileList").permitAll()
                .antMatchers("/download/file").permitAll()

                .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER","ROLE_FTP")
                .antMatchers("/REST/panel/**").hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER","ROLE_FTP")
                .antMatchers("/REST/admin/**").hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER","ROLE_FTP")
                .antMatchers("/REST/editor/save").hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER","ROLE_FTP")
                .antMatchers("/admin/uploadFile").hasAnyAuthority("ROLE_ADMIN","ROLE_MANAGER","ROLE_FTP")
                /*
                .antMatchers("/test").hasAuthority("ROLE_ADMIN")
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/images/*").permitAll()
                .antMatchers("/quill/**").permitAll()
                .antMatchers("/CSS/**").permitAll()
                .anyRequest().authenticated()*/
                .and()
                .formLogin()
        //.and()
        //.httpBasic()
                .loginPage("/login")
                //.loginProcessingUrl("/perform_login")
                //.failureUrl("/login.html"
                .failureUrl("/login?error=true")
                //.defaultSuccessUrl("/")
                .permitAll()
        ;

    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery(
                "select username,password,enabled from users where username=?"
        ).authoritiesByUsernameQuery(
                "select USERS.USERNAME, ROLE.NAME from USERS " +
                        "inner join users_roles on users.id = users_roles.user_id " +
                        "inner join role on users_roles.role_id = role.id " +
                        "WHERE USERS.USERNAME=?");

        }
            public static final String DEF_USERS_BY_USERNAME_QUERY=
                "select username,password,enabled from users where username=?";
            public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
                    "select USERS.USERNAME, PRIVILEGE.NAME from USERS " +
                            "inner join users_roles on users.id = users_roles.user_id " +
                            "inner join role on users_roles.role_id = role.id " +
                            "inner join ROLES_PRIVILEGES ON ROLES_PRIVILEGES.ROLE_ID=ROLE.ID " +
                            "INNER JOIN PRIVILEGE ON ROLES_PRIVILEGES.PRIVILEGE_ID = PRIVILEGE.ID " +
                            "WHERE USERS.USERNAME = ?";
            public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
                    "select g.id, g.group_name, ga.authority " +
                            "from groups g, group_members gm, group_authorities ga " +
                            "where gm.username = ? " +
                            "and g.id = ga.group_id " +
                            "and g.id = gm.group_id";

}

