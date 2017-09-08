package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepo extends CrudRepository<Role, Long> {

    Role findByRole(String role);

}
