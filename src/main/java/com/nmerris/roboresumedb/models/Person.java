package com.nmerris.roboresumedb.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.thymeleaf.expression.Strings;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Person implements Comparable<Person> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NotEmpty
    @Size(max = 50)
    private String nameFirst;

    @NotEmpty
    @Size(max = 50)
    private String nameLast;

    @Column(nullable = false)
    @NotEmpty
    @Email
    @Size(max = 50)
    private String email;

    @NotEmpty
    private String password;

    // all usernames must be unique
    @NotEmpty
    @Column(unique = true)
    private String username;

    private boolean enabled;

    @OneToMany(mappedBy = "myPerson", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<EducationAchievement> educationAchievements;

    @OneToMany(mappedBy = "myPerson", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<WorkExperience> workExperiences;

//    @OneToMany(mappedBy = "myPerson", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
//    private Set<Skill> skills;

    @OneToMany(mappedBy = "myPerson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Job> jobs;



    // Role is owner of Person
    // for the love of everything holy, MUST MUST MUST use EAGER here, or you can't log in at all!!
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    // Skill is owner of Person
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Collection<Skill> skills;





    public Person() {
        educationAchievements = new HashSet<>();
        workExperiences = new HashSet<>();
        skills = new HashSet<>();
        roles = new HashSet<>();
        jobs = new HashSet<>();
    }


    // just compare by last name, sort in ascending order
    @Override
    public int compareTo(Person other) {
        return getNameLast().compareToIgnoreCase(other.getNameLast());
    }




    // helper/convenience methods =================================================================================
    // add a single role to this Person
    public void addRole(Role role) {
        roles.add(role);
    }

    // remove a single role from this Person
    public void removeRole(Role role) {
        roles.remove(role);
    }

    // this app only allows a Person to have one role
    public String getRole() {
        for (Role r : roles) {
            if(r.getRole().equals("ROLE_USER")) return "ROLE_USER";
            if(r.getRole().equals("ROLE_RECRUITER")) return "ROLE_RECRUITER";
            // COULD ADD ADMIN ROLE HERE
        }
        // should never happen
        return null;
    }

    public String getFullName() {
        return nameFirst + " " + nameLast;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    // normal getters/setters =================================================================================
    // in order to delete an ed, you must first remove it from it's parents collection
    public void removeEdAchievement(EducationAchievement ea) {
        educationAchievements.remove(ea);
    }

    // need to use @Transactional annotation on any method that calls this
    public void removeAllEdAchievements() {
        educationAchievements.clear();
    }

    public void removeWorkExperience(WorkExperience we) {
        workExperiences.remove(we);
    }

    public void removeAllWorkExperiences() {
        workExperiences.clear();
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }

    public void removeAllSkills() {
        skills.clear();
    }

    public void removeJob(Job job) {
        jobs.remove(job);
    }




    public Set<WorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(Set<WorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public Collection<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public Set<EducationAchievement> getEducationAchievements() {
        return educationAchievements;
    }

    public void setEducationAchievements(Set<EducationAchievement> educationAchievements) {
        this.educationAchievements = educationAchievements;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
