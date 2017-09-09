package com.nmerris.roboresumedb.models;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Job implements Comparable<Job> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    @Size(max = 50)
    private String title;

    @NotEmpty
    @Size(max = 50)
    private String employer;

//    @Min(0)
    @Max(1000000)
    private long minSalary;

//    @Min(0)
    @Max(1000000)
    private long maxSalary;

    @NotEmpty
    private String description;

    // Skill is owner of Job
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Collection<Skill> skills;

    // Person is the owner of Job
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person myPerson;


    // just compare by ids, could compare with anything we want
    @Override
    public int compareTo(Job other) {
        // returns negative number is this.getId < other.getId, 0 if equal
//        return Long.compare(this.getId(), other.getId());
        return getEmployer().compareToIgnoreCase(other.getEmployer());

    }




    public Job() {
        skills = new HashSet<>();
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(long minSalary) {
        this.minSalary = minSalary;
    }

    public long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Collection<Skill> skills) {
        this.skills = skills;
    }

    public Person getMyPerson() {
        return myPerson;
    }

    public void setMyPerson(Person myPerson) {
        this.myPerson = myPerson;
    }
}
