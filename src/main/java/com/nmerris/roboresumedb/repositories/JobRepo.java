package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobRepo extends CrudRepository<Job, Long> {
}
