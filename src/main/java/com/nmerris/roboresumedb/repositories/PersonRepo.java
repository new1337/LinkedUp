package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public interface PersonRepo extends CrudRepository<Person, Long> {

    Person findByUsername(String username);

    Person findByEmail(String email);

    Long countByEmail(String email);

    Long countByUsername(String username);

    LinkedHashSet<Person> findByNameFirstIsAndNameLastIsOrderByNameLastAsc(String firstName, String lastName);

    // if user enters only one name, assume it could be either a first or last name
    LinkedHashSet<Person> findByNameFirstIsOrNameLastIsOrderByNameLastAsc(String name, String sameName);

}