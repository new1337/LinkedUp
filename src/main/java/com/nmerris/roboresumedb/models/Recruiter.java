package com.nmerris.roboresumedb.models;


import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable=false)
    @Email
    @NotEmpty
    private String email;

    @Column()
    @NotEmpty
    private String password;

    @Column
    @NotEmpty
    private String firstName;

    @Column
    @NotEmpty
    private String lastName;

    @Column
    private boolean enabled;

    @Column
    @NotEmpty
    private String username;

    // one recruiter can have many jobs
    @OneToMany(mappedBy = "myRecruiter", cascade = CascadeType.ALL, fetch= FetchType.EAGER)
    private Set<Job> jobs;


    // a recruiter can have many roles and many roles can belong to any number of recruiters
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;


    public Recruiter() {
        roles = new HashSet<>();
        jobs = new HashSet<>();
    }

    // TODO add helper add/remove methods


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Job> getJob() {
        return jobs;
    }

    public void setJob(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(HashSet<Role> roles) {
        this.roles = roles;
    }
}
