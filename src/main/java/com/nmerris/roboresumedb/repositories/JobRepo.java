package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Job;
import com.nmerris.roboresumedb.models.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.method.P;
import sun.awt.image.ImageWatched;

import java.util.Collection;
import java.util.LinkedHashSet;

public interface JobRepo extends CrudRepository<Job, Long> {

    Collection<Job> findAllByMyPersonIs(Person person);

    LinkedHashSet<Job> findByEmployerContainingOrderByEmployerAsc(String company);

}
