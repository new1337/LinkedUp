package com.nmerris.roboresumedb.services;

import com.nmerris.roboresumedb.models.Person;
import com.nmerris.roboresumedb.models.Role;
import com.nmerris.roboresumedb.repositories.PersonRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Transactional // either do it all, or don't do any of it, prevents partial db persisting
@Service
public class SSUserDetailsService implements UserDetailsService {

    private PersonRepo personRepo;

    public SSUserDetailsService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            Person user = personRepo.findByUsername(username);
            if(user == null) {
                // in real life, don't let anyone know that user was not found, just say something generic like "invalid login"
                System.out.println("!!!!!!!!!!!!! user not found with username: " + user.getUsername());
//                System.out.println("!!!!!!!!!!!!! user not found with username: " + user.toString());

                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // HERE is where you check if the user is 'enabled' or not, if user is not 'enabled', then return null

                return null;
            }

            System.out.println("============= inside SSUserDetailsService.loadUserByUsername... found user with username: " + user.getUsername());
//            System.out.println("============= found user with username: " + user.toString());

            // User here is NOT the same as our User entity
            return new org.springframework.security.core.userdetails.User(user.getUsername(),
                    user.getPassword(), getAuthorities(user));
        } catch (Exception e) {
            throw new UsernameNotFoundException("ThAt UsEr WaS nOt FoUnD (in SSUserDetailsService)");
        }

    }


    // GrantedAuthoriy is a special SS object that tells spring what paths the user can access
    // we are NOT actually changing anything in the dbs here, we are just pulling from userrepo, which should already
    // have Users with atached Roles that we previously entered (prob. via a web form).  We are just telling SS what
    // granted authorities user has, this is part of the magic that allows SecurityConfiguration to restrict or grant
    // access to various paths in our app
    private Set<GrantedAuthority> getAuthorities(Person user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : user.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getRole());
            authorities.add(grantedAuthority);
        }

        System.out.println("==================== inside SSUserDetailsService.loadUserByUsername... user authorities are: " + authorities.toString());
        return authorities;
    }

}
