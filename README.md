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

-- (id, rating, skill) 
INSERT INTO skill VALUES(1, 'Expert', 'Java programming');
INSERT INTO skill VALUES(2, 'Proficient', 'Java programming');
INSERT INTO skill VALUES(3, 'Familiar', 'Java programming');
INSERT INTO skill VALUES(4, 'Expert', 'Bike Riding');
INSERT INTO skill VALUES(5, 'Proficient', 'Bike Riding');
INSERT INTO skill VALUES(6, 'Familiar', 'Bike Riding');
INSERT INTO skill VALUES(7, 'Expert', 'Singing');
INSERT INTO skill VALUES(8, 'Proficient', 'Singing');
INSERT INTO skill VALUES(9, 'Familiar', 'Singing');
INSERT INTO skill VALUES(10, 'Expert', 'Creative Writing');
INSERT INTO skill VALUES(11, 'Proficient', 'Creative Writing');
INSERT INTO skill VALUES(12, 'Familiar', 'Creative Writing');

INSERT INTO person VALUES(1, 'a@b.com', 1, 'Jim', 'Bean', 'pass', 'rec');
INSERT INTO person VALUES(2, 'y@z.com', 1, 'Sue', 'Bee', 'pass', 'user');

INSERT INTO person_roles VALUES(1, 2);
INSERT INTO person_roles VALUES(2, 1);