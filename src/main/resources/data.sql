-- for sql database datasource example
CREATE TABLE `spring_batch`.`student` (
  `id` INT NOT NULL,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));


INSERT INTO spring_batch.student
VALUES (1, "John", "Smith", "jsmith@gmail.com");

INSERT INTO spring_batch.student
VALUES (2, "Jim", "Smith", "jim@gmail.com");

INSERT INTO spring_batch.student
VALUES (3, "Tim", "Mark", "timm@gmail.com");

INSERT INTO spring_batch.student
VALUES (4, "Ciel", "Noir", "cielnoir@gmail.com");

INSERT INTO spring_batch.student
VALUES (5, "Jimmy", "Dave", "jd@gmail.com");

INSERT INTO spring_batch.student
VALUES (6, "Jamie", "Foy", "foyj@gmail.com");

INSERT INTO spring_batch.student
VALUES (7, "Sarah", "Fox", "foxy@gmail.com");

INSERT INTO spring_batch.student
VALUES (8, "Sandra", "Ford", "fords@gmail.com");

INSERT INTO spring_batch.student
VALUES (9, "Peter", "Parker", "perterpark@gmail.com");

INSERT INTO spring_batch.student
VALUES (10, "Jim", "Flick", "jimflick@gmail.com");