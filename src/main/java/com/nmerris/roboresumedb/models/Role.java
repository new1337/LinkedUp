package com.nmerris.roboresumedb.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String role;

    // a role always 'owns' its Person or Recruiter, it's just more logical this way if you ask me
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Collection<Person> persons;

//    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
//    private Collection<Recruiter> recruiters;


    public Role() {
        persons = new HashSet<>();
//        recruiters = new HashSet<>();
    }

    // useful for debugging, remove for production
    @Override
    public String toString() {
        return role;
    }


    // helper methods... prob. don't need all these
    public void addPerson(Person user) {
        persons.add(user);
    }

    public void removePerson(Person user) {
        persons.remove(user);
    }

    public void addPersons(HashSet<Person> users) {
        this.persons.addAll(users);
    }

    public void removePersons(HashSet<Person> users) {
        this.persons.removeAll(users);
    }

//    public void addRecruiter(Recruiter recruiter) {
//        recruiters.add(recruiter);
//    }
//
//    public void removeRecruiter(Recruiter recruiter) {
//        recruiters.remove(recruiter);
//    }
//
//    public void addRecruiters(HashSet<Recruiter> recruiters) {
//        this.recruiters.addAll(recruiters);
//    }
//
//    public void removeRecruiters(HashSet<Recruiter> recruiters) {
//        this.recruiters.removeAll(recruiters);
//    }


    // normal getters/setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Collection<Person> getPersons() {
        return persons;
    }

    public void setPersons(HashSet<Person> users) {
        this.persons = users;
    }

//    public Collection<Recruiter> getRecruiters() {
//        return recruiters;
//    }
//
//    public void setRecruiters(HashSet<Recruiter> recruiters) {
//        this.recruiters = recruiters;
//    }


}
