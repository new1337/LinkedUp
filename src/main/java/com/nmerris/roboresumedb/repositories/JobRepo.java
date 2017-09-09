package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Job;
import com.nmerris.roboresumedb.models.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.method.P;

import java.util.Collection;

public interface JobRepo extends CrudRepository<Job, Long> {

    Collection<Job> findAllByMyPersonIs(Person person);

}
