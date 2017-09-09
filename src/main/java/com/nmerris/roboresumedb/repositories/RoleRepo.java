package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Person;
import com.nmerris.roboresumedb.models.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Set;

public interface RoleRepo extends CrudRepository<Role, Long> {

    Role findByRole(String role);

    Set<Role> findAllByPersonsIs(Person person);

}

