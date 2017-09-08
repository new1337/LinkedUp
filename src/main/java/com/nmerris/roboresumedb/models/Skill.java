package com.nmerris.roboresumedb.models;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;

@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    @Size(max = 50)
    @Column(unique = true)
    private String skill;

    @NotEmpty
    private String rating;

    // Skill owns Job
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private Collection<Job> jobs;

    // Skill also owns Person
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private Collection<Person> persons;

//
//    public Job getMyJob() {
//        return myJob;
//    }
//
//    public void setMyJob(Job myJob) {
//        this.myJob = myJob;
//    }
//
//    public Person getMyPerson() {
//        return myPerson;
//    }
//
//    public void setMyPerson(Person myPerson) {
//        this.myPerson = myPerson;
//    }


    public Skill() {
        jobs = new HashSet<>();
        persons = new HashSet<>();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Collection<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Collection<Job> jobs) {
        this.jobs = jobs;
    }

    public Collection<Person> getPersons() {
        return persons;
    }

    public void setPersons(Collection<Person> persons) {
        this.persons = persons;
    }
}
