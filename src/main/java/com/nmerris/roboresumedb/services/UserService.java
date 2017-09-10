package com.nmerris.roboresumedb.services;

import com.nmerris.roboresumedb.models.Person;
import com.nmerris.roboresumedb.repositories.PersonRepo;
import com.nmerris.roboresumedb.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    PersonRepo personRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    public UserService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }


    public Person findByUsername(String username) {
        return personRepo.findByUsername(username);
    }

    public Long countByEmail(String email) {
        return personRepo.countByEmail(email);
    }

    public Person findByEmail(String email) {
        return personRepo.findByEmail(email);
    }

    // returns 0 if any problems (ie if username already taken), 1 if ok
    public int saveUser(Person user) {
        System.out.println("============================= UserService.saveUser, just got user.username: " + user.getUsername());

        if(personRepo.countByUsername(user.getUsername()) > 0) {
            // username already exists, display an appropriate error message
            return 0;
        }
        user.addRole(roleRepo.findByRole("ROLE_USER"));
        // overrode toString in Role, so should print out role string here
        System.out.println("=================== adding role to user: " + roleRepo.findByRole("ROLE_USER"));
        user.setEnabled(true);
        personRepo.save(user);
        System.out.println("=================== just saved user to repo");
        return 1;
    }

    // returns 0 if any problems (ie if username already taken), 1 if ok
    public int saveRecruiter(Person user) {
        System.out.println("============================= UserService.saveRecruiter, just got user.username: " + user.getUsername());

        if(personRepo.countByUsername(user.getUsername()) > 0) {
            // username already exists, display an appropriate error message
            return 0;
        }


        user.addRole(roleRepo.findByRole("ROLE_RECRUITER"));
        // overrode toString in Role, so should print out role string here
        System.out.println("=================== adding role to user: " + roleRepo.findByRole("ROLE_RECRUITER"));
        user.setEnabled(true);
        personRepo.save(user);
        System.out.println("=================== just saved user to repo");
        return 1;
    }

}
