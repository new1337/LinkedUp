package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Person;
import com.nmerris.roboresumedb.models.Skill;
import org.springframework.data.repository.CrudRepository;

public interface SkillRepo extends CrudRepository<Skill, Long> {

    long countAllByIdIs(long id);

    Skill findBySkillIsAndRatingIs(String name, String rating);

}