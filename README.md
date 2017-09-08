# StudentResumeBot

This web app simulates an administrative student course and resume tool.  An admin user logs in and add students and courses.  A postgresql database is used to persist the data.  There are five relational tables, all implemented using the Java persistence framework.  Students and Courses share a many to many relationship.. a student can be enrolled in many courses, and a course can have many registered students.  The admin can selective remove students from a course, one or many at a time.  The admin may also simultaneously remove one or many courses from a students registration list.  The app also allows the user to create a Resume for each student, where numerous educational achievements, work experiences, and skills can all be entered.  A final resume is generated after the user enters at least one skill and one educational achievement.  All database entities can be edited and removed at will, while all relationships are retained.

It is responsive and mobile ready!

This readme is under construction!

To see the app live on heroku:

https://gentle-chamber-81023.herokuapp.com/

username: user, password: pass

db seed sql:

INSERT INTO role VALUES(1, 'ROLE_USER');
INSERT INTO role VALUES(2, 'ROLE_RECRUITER');
INSERT INTO role VALUES(3, 'ROLE_ADMIN');

-- (id, rating, skill, person_id) 
INSERT INTO skill VALUES(1, null, 'Java Programming', null);
INSERT INTO skill VALUES(2, null, 'Bike Riding', null);
INSERT INTO skill VALUES(3, null, 'Singing', null);
INSERT INTO skill VALUES(4, null, 'Creative Writing', nul
-- etc