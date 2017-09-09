package com.nmerris.roboresumedb.controllers;

import com.nmerris.roboresumedb.NavBarState;
import com.nmerris.roboresumedb.Utilities;
import com.nmerris.roboresumedb.models.*;
import com.nmerris.roboresumedb.repositories.*;
import com.nmerris.roboresumedb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;


@Controller
public class MainController {

    @Autowired
    PersonRepo personRepo;
    @Autowired
    EducationRepo educationRepo;
    @Autowired
    SkillRepo skillRepo;
    @Autowired
    WorkExperienceRepo workExperienceRepo;
    @Autowired
    JobRepo jobRepo;
    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UserService userService;


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", "Please log in or register");

        // after successfully logging in, user will see their summary page via the /summary route
        // there is no login post route, it is never called, SecurityConfiguration class sets the default route
        // after logging in
        return "login";
    }



    @GetMapping("/summary")
    public String summary(Model model, Principal principal) {
        System.out.println("=============================================================== just entered /summary GET");
        System.out.println("=========================================== Principal.getName (username): " + principal.getName());
        System.out.println("=========================================== personRepo.findByUserName.getUsername: " + personRepo.findByUsername(principal.getName()).getUsername());
        System.out.println("=========================================== personRepo.findByUserName.getRole: " + personRepo.findByUsername(principal.getName()).getRole());

        // show them a summary page based on their role
        switch(personRepo.findByUsername(principal.getName()).getRole()) {
            case "ROLE_USER" :

                return "redirect:/editdetails";

            case "ROLE_RECRUITER" :

                model.addAttribute("message", personRepo.findByUsername(principal.getName()).getFullName() + "'s job postings");
                model.addAttribute("person", personRepo.findByUsername(principal.getName()));
                model.addAttribute("highLightPostJob", false);
                model.addAttribute("highLightPostList", true);
                model.addAttribute("highLightSearch", false);

                return "summaryrecruiter";
        }



        // should never happen
        // TODO need a custom error page instead of redirecting randomly
        return "redirect:/";
    }


    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

    // default route takes user to addperson, but since basic authentication security is enabled, they will have to
    // go through the login route first, then Spring will automatically take them to addperson
    @GetMapping("/")
    public String indexPageGet() {
        // TODO might be nice to show the count of job postings, persons, recruiters in 'welcome/login' page

        return "welcome";
    }


    @GetMapping("/register")
    public String registerGet(Model model) {
        model.addAttribute("newPerson", new Person());
        return "register";
    }


    @PostMapping("/register")
    public String processRegistration(@RequestParam(value = "selectedRole") String role,
                                      @Valid @ModelAttribute("newPerson") Person user,
                                      BindingResult bindingResult,
                                      Model model) {

        System.out.println("####################### /register POST... incoming role String is: " + role);

        // always add the incoming user back to the model
        model.addAttribute("newPerson", user);

        if(bindingResult.hasErrors()) {
            return "registration";
        }
        else {
            if(role.equals("ROLE_USER")) {
                userService.saveUser(user);
            }
            else {
                // must be ROLE_RECRUITER
                userService.saveRecruiter(user);
            }
        }

        // nice to have a message to confirm that registration was ok
        model.addAttribute("message", "Thank you for registering "
                + user.getNameFirst() + " " + user.getNameLast() + ", please log in");

        // always need to login after registering
        // after successfully logging in, user will see their summary page via the /summary route
//        return "redirect:/";
        return "login";

    }


    // wipes all the skills, work experiences, and eds from current Person, only used by job seekers
    @GetMapping("/startover")
    // Transactional is necessary to call removeAllBy.. on the repos
    // PersistenceContext defaults to PersistenceContextType.TRANSACTION, thank you Stack Overflow!
    @Transactional
    public String startOver(Principal principal) {
        // remove all items from Person
        Person p = personRepo.findByUsername(principal.getName());
        p.removeAllEdAchievements();
        p.removeAllWorkExperiences();
        p.removeAllSkills();

        // remove all items from repos
        educationRepo.removeAllByMyPersonIs(p);
        workExperienceRepo.removeAllByMyPersonIs(p);

        // remove this Person from each Skill associated with this Person
        for (Skill skill : p.getSkills()) {
            skill.removePerson(p);
        }
        // remove all this Persons skills
        p.getSkills().clear();
        // save this Person, now should have not eds, workExps, or skills
        personRepo.save(p);

        return "redirect:/editdetails";
    }


    @GetMapping("/joblist")
    public String jobListGet(Model model, Principal principal) {
        System.out.println("=============================================================== just entered /joblist GET");
        System.out.println("=========================================== principal.getName: " + principal.getName());


//        model.addAttribute("jobs", jobRepo.findAllByMyPersonIs(personRepo.findByUsername(principal.getName())));
        model.addAttribute("message", "Your job postings");
        model.addAttribute("person", personRepo.findByUsername(principal.getName()));

        model.addAttribute("highLightPostJob", false);
        model.addAttribute("highLightPostList", true);
        model.addAttribute("highLightSearch", false);

        return "summaryrecruiter";
    }


    @GetMapping("/addjob")
    public String addJobGet(Model model) {
        System.out.println("=============================================================== just entered /addJob GET");

        model.addAttribute("newJob", new Job());
        model.addAttribute("skills", skillRepo.findAll());
        model.addAttribute("highLightPostJob", true);
        model.addAttribute("highLightPostList", false);
        model.addAttribute("highLightSearch", false);

        return "addjob";
    }


    @PostMapping("/addjob")
    public String addJobPost(@Valid @ModelAttribute("newJob") Job job,
                             @RequestParam(value = "checkedIds", required = false) long[] checkedIds,
                             BindingResult bindingResult, Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addJob POST");


        if(bindingResult.hasErrors()) {


            return "addjob";
        }

        if(checkedIds != null) {
            for (long id : checkedIds) {
                job.addSkill(skillRepo.findOne(id));
            }
        }


        job.setMyPerson(personRepo.findByUsername(principal.getName()));

        jobRepo.save(job);

//        model.addAttribute("message", "Successfully posted a new job");

        model.addAttribute("jobJustAdded", job);
        model.addAttribute("highLightPostJob", true);
        model.addAttribute("highLightPostList", false);
        model.addAttribute("highLightSearch", false);


        return "addjobconfirmation";
    }


//    @GetMapping("/addperson")
//    public String addPersonGet(Model model) {
//        System.out.println("=============================================================== just entered /addperson GET");
//
//        // send the existing person to the form
//        model.addAttribute("newPerson", personRepo.findOne(currPerson.getPersonId()));
//
//        NavBarState pageState = getPageLinkState();
//        // set the navbar to highlight the appropriate link
//        pageState.setHighlightPersonNav(true);
//        model.addAttribute("pageState", pageState);
//
//        return "addperson";
//    }


    @PostMapping("/addperson")
    public String addPersonPost(@Valid @ModelAttribute("newPerson") Person personFromForm,
                                BindingResult bindingResult, Model model, Principal principal, Authentication auth) {
        System.out.println("=============================================================== just entered /addperson POST");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

//        Person p = personRepo.findByUsername(principal.getName());

        // return the same view (now with validation error messages) if there were any validation problems
        if(bindingResult.hasErrors()) {
            // always need to set up the navbar, every time a view is returned
            NavBarState pageState = getPageLinkState(personFromForm);
            pageState.setHighlightPersonNav(true);
            model.addAttribute("pageState", pageState);
            return "addperson";
        }

//        p.setNameFirst(personFromForm.getNameFirst());
//        p.setNameLast(personFromForm.getNameLast());
//        p.setEmail(personFromForm.getEmail());

        // ugly business here... need a better way to to do this...
        // the parent relationship data is lost when the Person comes back from the form, so need to reattach is all
        // here or it goes poof (and would loose skills and roles)

//        Set<Role> rolesToPreserve = roleRepo.findAllByPersonsIs(personFromForm);
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! got these roles for personFromForm: " + rolesToPreserve);
        personFromForm.setRoles(roleRepo.findAllByPersonsIs(personFromForm));

//        personFromForm.addRole(personRepo.findByUsername(principal.getName()).getRole());
        personRepo.save(personFromForm);

        // go to education section automatically, it's the most logical
        // since there is no confirmation page for addperson, we want to redirect here
        // redirect means that if this route gets to this point, it's not even going to return a view at all, which
        // is why no model stuff is needed here, redirect is basically like clicking on a link on a web page
        // you can redirect to any internal route, or any external URL
        return "redirect:/addeducation";
    }


    @GetMapping("/addeducation")
    public String addEdGet(Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addeducation GET");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        // get the current Person
//        Person p = personRepo.findOne(currPerson.getPersonId());
        Person p = personRepo.findByUsername(principal.getName());


        // disable the submit button if >= 10 records in db, it would never be possible for the user to click to get
        // here from the navi page if there were already >= 10 records, however they could manually type in the URL
        // so I want to disable the submit button if they do that and there are already 10 records
        model.addAttribute("disableSubmit", educationRepo.countAllByMyPersonIs(p) >= 10);
//        model.addAttribute("disableSubmit", educationRepo.count() >= 10);

        // each resume section (except personal) shows a running count of the number of records currently in the db
        model.addAttribute("currentNumRecords", educationRepo.countAllByMyPersonIs(p)); // where is my cute little 'o:'?

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightEdNav(true);
        model.addAttribute("pageState", pageState);

        model.addAttribute("firstAndLastName", p.getFullName());

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% created new ea, attached currPerson to it, about to add it to model");
        // create a new ea, attach the curr person to it, and add it to model
        EducationAchievement ea = new EducationAchievement();
        ea.setMyPerson(p);
        model.addAttribute("newEdAchievement", ea);

        return "addeducation";
    }

    
    @PostMapping("/addeducation")
    public String addEdPost(@Valid @ModelAttribute("newEdAchievement") EducationAchievement educationAchievement,
                            BindingResult bindingResult, Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addeducation POST");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        // get the current Person
//        Person p = personRepo.findOne(currPerson.getPersonId());
        Person p = personRepo.findByUsername(principal.getName());

        // get the current count from educationRepo for the current Person
        long count = educationRepo.countAllByMyPersonIs(p);
        System.out.println("=========================================== repo count for currPerson is: " + count);

        // the persons name is show at the top of each 'add' section AND each confirmation page, so we want to add
        // it to the model no matter which view is returned
//        addPersonNameToModel(model);
        model.addAttribute("firstAndLastName", p.getFullName());

        // return the same view (now with validation error messages) if there were any validation problems
        if(bindingResult.hasErrors()) {
            // update the navbar state and add it to our model
            NavBarState pageState = getPageLinkState(p);
            pageState.setHighlightEdNav(true);
            model.addAttribute("pageState", pageState);

            // disable the form submit button if there are 10 or more records in the education repo
            model.addAttribute("disableSubmit", count >= 10);
            model.addAttribute("currentNumRecords", count);

            return "addeducation";
        }

        // I'm being picky here, but it is possible for the user to refresh the page, which bypasses the form submit
        // button, and so they would be able to add more than 10 items, to avoid this, just condition the db save on count
        if(count < 10) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% about to save ea to educationRepo");
            educationRepo.save(educationAchievement);

            // need to get an updated edsCount after saving to repo
            count = educationRepo.countAllByMyPersonIs(p);
            System.out.println("=========================================== repo count for currPerson is: " + count);
        }

        // need to get the count AFTER successfully adding to db, so it is up to date
        model.addAttribute("currentNumRecords", count);

        // add the EducationAchievement just entered to the model, so we can show a confirmation page
        model.addAttribute("edAchievementJustAdded", educationAchievement);
        
        // also need to set disableSubmit flag AFTER adding to db, or user will think they can add more than 10
        // because the 'add another' button will work, but then the entry form button will be disabled, this
        // way the user will not be confused... I am repurposing 'disableSubmit' here, it's actually being used to
        // disable the 'Add Another' button in the confirmation page
        model.addAttribute("disableSubmit", count >= 10);

        // the navbar state depends on the db table counts in various ways, so update after db changes
        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightEdNav(true);
        model.addAttribute("pageState", pageState);

        return "addeducationconfirmation";
    }


    // logic in this route is identical to /addeducation, see /addeducation GetMapping for explanatory comments
    @GetMapping("/addworkexperience")
    public String addWorkGet(Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addworkexperience GET");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        // get the current Person
//        Person p = personRepo.findOne(currPerson.getPersonId());
        Person p = personRepo.findByUsername(principal.getName());
        model.addAttribute("disableSubmit", workExperienceRepo.countAllByMyPersonIs(p) >= 10);
        model.addAttribute("currentNumRecords", workExperienceRepo.countAllByMyPersonIs(p));

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightWorkNav(true);
        model.addAttribute("pageState", pageState);
        model.addAttribute("firstAndLastName", p.getFullName());

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% created new workExp, attached currPerson to it, about to add it to model");
        WorkExperience workExp = new WorkExperience();
        workExp.setMyPerson(p);
        model.addAttribute("newWorkExperience", workExp);

        return "addworkexperience";
    }
    
    
    // logic in this route is identical to /addeducation, see /addeducation PostMapping for explanatory comments
    @PostMapping("/addworkexperience")
    public String addWorkPost(@Valid @ModelAttribute("newWorkExperience") WorkExperience workExperience,
                            BindingResult bindingResult, Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addworkexperience POST");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        // get the current Person
        Person p = personRepo.findByUsername(principal.getName());

        // get the current count from work repo for the current Person
        long count = workExperienceRepo.countAllByMyPersonIs(p);
        System.out.println("=========================================== repo count for currPerson is: " + count);

        model.addAttribute("firstAndLastName", p.getFullName());


        if(bindingResult.hasErrors()) {
            NavBarState pageState = getPageLinkState(p);
            pageState.setHighlightWorkNav(true);
            model.addAttribute("pageState", pageState);
            model.addAttribute("currentNumRecords", count);
            model.addAttribute("disableSubmit", count >= 10);

            return "addworkexperience";
        }

        if(count < 10) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% about to save workExp to workExpRepo");
            workExperienceRepo.save(workExperience);

            count = workExperienceRepo.countAllByMyPersonIs(p);
            System.out.println("=========================================== repo count for currPerson is: " + count);
        }

        model.addAttribute("currentNumRecords", count);

        // work experience end date can be left null by user, in which case we want to show 'Present' in the
        // confirmation page
        model.addAttribute("dateEndString", Utilities.getMonthDayYearFromDate(workExperience.getDateEnd()));
        model.addAttribute("workExperienceJustAdded", workExperience);
        model.addAttribute("disableSubmit", count >= 10);

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightWorkNav(true);
        model.addAttribute("pageState", pageState);

        return "addworkexperienceconfirmation";
    }

    
    // logic in this route is identical to /addeducation, see /addeducation GetMapping for explanatory comments
    @GetMapping("/addskill")
    public String addSkillGet(Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addskill GET");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        // get the current Person
        Person p = personRepo.findByUsername(principal.getName());

        model.addAttribute("disableSubmit", p.getSkills().size() >= 20);
        model.addAttribute("currentNumRecords", p.getSkills().size());

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightSkillNav(true);
        model.addAttribute("pageState", pageState);

        model.addAttribute("firstAndLastName", p.getFullName());


        // make a Set of skill names, no duplicates in a set, user can pick from these, and also pick a rating
        Set<String> skillNames = new HashSet<>();
        for (Skill skill : skillRepo.findAll()) {
            skillNames.add(skill.getSkill());
        }

        model.addAttribute("skillNames", skillNames);

        return "addskill";
    }


    // no BindingResult necessary because user options are drop down and prechecked radio, so can't be null/empty
    @PostMapping("/addskill")
    public String addSkillPost(@RequestParam(value = "selectedSkillName", required = false) String selectedSkillName,
                               @RequestParam(value = "rating", required = false) String rating,
                                Model model, Principal principal) {
        System.out.println("=============================================================== just entered /addskill POST");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! got newSkill with name: " + selectedSkillName);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! got newSkill with rating: " + rating);


        // get the current Person
        Person p = personRepo.findByUsername(principal.getName());

        // get the current count from work repo for the current Person
        long count = p.getSkills().size();
        System.out.println("=========================================== repo count for currPerson is: " + count);

        model.addAttribute("firstAndLastName", p.getFullName());

        Skill skillToAddToPerson = skillRepo.findBySkillIsAndRatingIs(selectedSkillName, rating);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! skillRepo.findByBlahBlah id: " + skillToAddToPerson.getId());


        if(count < 20) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% about to save skill to Repo");

            p.addSkill(skillToAddToPerson);
            personRepo.save(p);

            count = p.getSkills().size();
            System.out.println("=========================================== repo count for currPerson is: " + count);
        }

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightSkillNav(true);
        model.addAttribute("pageState", pageState);

        model.addAttribute("currentNumRecords", count);
        model.addAttribute("skillJustAdded", skillToAddToPerson);
        model.addAttribute("disableSubmit", count >= 20);

        return "addskillconfirmation";
    }


    // this route returns a view that shows ALL the records from every repo
    // every record can be edited by clicking a link next to it
    // every record (except the single personal details record) can also be deleted by clicking a link next to it
    @GetMapping("/editdetails")
    public String editDetails(Model model, Principal principal) {
        System.out.println("=============================================================== just entered /editdetails GET");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());

        // get the current Person
        Person p = personRepo.findByUsername(principal.getName());
        model.addAttribute("person", p);
        model.addAttribute("edAchievements", educationRepo.findAllByMyPersonIs(p));
        model.addAttribute("workExperiences", workExperienceRepo.findAllByMyPersonIs(p));
        model.addAttribute("skills", p.getSkills());

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightEditNav(true);
        model.addAttribute("pageState", pageState);

        return "editdetails";
    }


    // id is the id to delete
    // type is what table to delete from
    // this route is triggered when the user clicks on the 'delete' link next to a row in editdetails.html
    // no model is needed here because all the returned views are redirects
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id, @RequestParam("type") String type, Principal principal)
    {
        System.out.println("=============================================================== just entered /delete/{id} GET");
//        System.out.println("=========================================== currPerson.getPersonId(): " + currPerson.getPersonId());
        System.out.println("=========================================== incoming path var Id: " + id);

        Person p = personRepo.findByUsername(principal.getName());

        try {
            switch (type) {
                case "ed" :
                    // remove the ed from person, then delete it from it's repo
                    p.removeEdAchievement(educationRepo.findOne(id));
                    educationRepo.delete(id);
                    // return with an anchor tag so that the user is still at the same section after deleting
                    // this is not perfect, but it's better than jumping to the top of the page each time
                    return "redirect:/editdetails#education";
                case "person" :
                    // TODO needs work, sometimes has foreign key problems

//                    Person pToDelete = personRepo.findOne(id);
//                    for (Course c : pToDelete.getCourses()) {
//                        courseRepo.findOne(c.getId()).removePerson(pToDelete);
//                    }
//
//
//                    pToDelete.removeAllCourses();
//                    pToDelete.removeAllSkills();
//                    pToDelete.removeAllWorkExperiences();
//                    pToDelete.removeAllEdAchievements();
//
//                    personRepo.delete(id); // is this all?
                    return "redirect:/";
                case "workexp" :
                    p.removeWorkExperience(workExperienceRepo.findOne(id));
                    workExperienceRepo.delete(id);
                    return "redirect:/editdetails#workexperiences";
                case "skill" :
                    p.removeSkill(skillRepo.findOne(id));
                    personRepo.save(p);
                    return "redirect:/editdetails#skills";
                case "job" :
                    // get the job in question
                    Job job = jobRepo.findOne(id);

                    // remove the skills from the job... necessary? don't think so....
//                    job.getSkills().clear();

                    // remove the job from it's person
                    p.removeJob(job);

                    // remove this job from all Skills that associate with it
                    for (Skill skill : skillRepo.findAll()) {
                        skill.removeJob(job);
                    }

                    // delete it
                    jobRepo.delete(id);

                    return "redirect:/summary";
            }
        } catch (Exception e) {
            // need to catch an exception that may be thrown if user refreshes the page after deleting an item.
            // refreshing the page will attempt to delete the same ID from the db, which will not exist anymore if
            // they just deleted it.  catching the exception will prevent the app from crashing, and the same page
            // will simply be redisplayed
        }

        // should never happen, but need it to compile, better to redirect, just in case something does go wrong, at
        // least this way the app will not crash
        return "redirect:/editdetails";
    }


    // id is the id to update
    // type is what table to update
    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") long id, @RequestParam("type") String type, Model model, Principal principal)
    {
        System.out.println("=============================================================== just entered /update/{id} GET");
//        System.out.println("=========================================== currPerson.getPersonId() initially: " + currPerson.getPersonId());

        // set the current person ID to the incoming path variable IF type is person or student
        Person p = personRepo.findByUsername(principal.getName());

        // no matter what view is returned, we ALWAYS will allow the submit button to work, since the form that is
        // displays can only contain a record that already exists in a repo
        model.addAttribute("disableSubmit", false);
        model.addAttribute("firstAndLastName", p.getFullName());

        NavBarState pageState = getPageLinkState(p);

        switch (type) {
            case "person" :
                // get the appropriate record from the repo
                model.addAttribute("newPerson", p);
                // set the appropriate nav bar highlight
                pageState.setHighlightPersonNav(true);
                // add the navbar state object to the model
                model.addAttribute("pageState", pageState);
                // return the appropriate view
                return "addperson";
            case "ed" :
                model.addAttribute("newEdAchievement", educationRepo.findOne(id));
                model.addAttribute("currentNumRecords", educationRepo.countAllByMyPersonIs(p));
                pageState.setHighlightEdNav(true);
                model.addAttribute("pageState", pageState);
                return "addeducation";
            case "workexp" :
                model.addAttribute("newWorkExperience", workExperienceRepo.findOne(id));
                model.addAttribute("currentNumRecords", workExperienceRepo.countAllByMyPersonIs(p));
                pageState.setHighlightWorkNav(true);
                model.addAttribute("pageState", pageState);
                return "addworkexperience";
            case "skill" :
                model.addAttribute("newSkill", skillRepo.findOne(id));
                model.addAttribute("currentNumRecords", p.getSkills().size());
                pageState.setHighlightSkillNav(true);
                model.addAttribute("pageState", pageState);
                return "addskill";
            case "job" :
                model.addAttribute("newJob", jobRepo.findOne(id));
                model.addAttribute("skills", skillRepo.findAll());
                model.addAttribute("highLightPostJob", true);
                model.addAttribute("highLightPostList", false);
                model.addAttribute("highLightSearch", false);
                model.addAttribute("showDelete", true);
                return "addjob";
        }

        // should never happen, but need it to compile, better to redirect, just in case something does go wrong, at
        // least this way the app will not crash
        return"redirect:/editdetails";
    }


    @GetMapping("/finalresume")
    public String finalResumeGet(Model model, Principal principal) {
        Person p = personRepo.findByUsername(principal.getName());

        NavBarState pageState = getPageLinkState(p);
        pageState.setHighlightFinalNav(true);
        model.addAttribute("pageState", pageState);

        model.addAttribute("person", p);

        return "finalresume";
    }





        /**
         * The navbar links are disabled depending on the number of records in the various db tables.  For example, we
         * do not want to allow the user to click the EditDetails link if there are no records in any db table.
         * Note: the 'highlighted' nav bar link is set individually in each route.  Also, the navbar links contain badges
         * that show the current counts for various db tables.  These counts are updated here and will always reflect the
         * current state of the db tables.
         * @return an updated NavBarState, but the highlighted navbar link must still be set individually
         */
    private NavBarState getPageLinkState(Person p) {
        NavBarState state = new NavBarState();

        // add the current table counts, so the navbar badges know what to display
        state.setNumSkills(p.getSkills().size());
        state.setNumWorkExps(workExperienceRepo.countAllByMyPersonIs(p));
        state.setNumEdAchievements(educationRepo.countAllByMyPersonIs(p));

        // disable links as necessary... don't allow them to click any links if the repos contain too many records
        state.setDisableAddEdLink(educationRepo.countAllByMyPersonIs(p) >= 10);
        state.setDisableAddSkillLink(p.getSkills().size() >= 20);
        state.setDisableAddWorkExpLink(workExperienceRepo.countAllByMyPersonIs(p) >= 10);

//        state.setDisableEditDetailsLink(false);

        // disable show final resume link until at least one ed achievement, skill, and personal info has been entered
        state.setDisableShowFinalLink(p.getSkills().size() == 0 || educationRepo.countAllByMyPersonIs(p) == 0);

        return state;
    }


}
