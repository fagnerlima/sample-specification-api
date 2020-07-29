CREATE EXTENSION unaccent;

CREATE TABLE tag (
  id SERIAL,
  description VARCHAR(64) NOT NULL,
  CONSTRAINT pk_tag PRIMARY KEY(id)
);

CREATE TABLE task (
  id SERIAL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  description VARCHAR(100) NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT pk_task PRIMARY KEY(id)
);

CREATE TABLE tag_task (
  id_tag INT NOT NULL,
  id_task INT NOT NULL,
  CONSTRAINT pk_tag_task PRIMARY KEY(id_tag, id_task),
  CONSTRAINT fk_tag_task_id_tag FOREIGN KEY(id_tag) REFERENCES tag(id)
    MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_tag_task_id_task FOREIGN KEY(id_task) REFERENCES task(id)
    MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO tag (description) VALUES
  ('Work'),
  ('Dev'),
  ('Study'),
  ('Entertainment'),
  ('Event'),
  ('Training');

INSERT INTO task (start_date, end_date, description, status) VALUES
  ('2020-08-08', '2020-08-08', 'My wife''s birthday', 'DONE'),
  ('2020-08-09', '2020-08-10', 'Freelancer 1', 'DONE'),
  ('2020-08-11', '2020-08-11', 'Running', 'CANCELED'),
  ('2020-08-11', '2020-08-13', 'Freelancer 2', 'PENDING'),
  ('2020-08-14', '2020-08-14', 'Swimming', 'PENDING'),
  ('2020-09-01', '2020-09-05', 'Workshop', 'PENDING'),
  ('2020-09-10', '2020-09-25', 'Spring course', 'PENDING'),
  ('2020-09-16', '2020-09-20', 'Angular Course', 'PENDING'),
  ('2020-09-21', '2020-09-25', 'Flutter couse', 'PENDING'),
  ('2020-10-15', '2020-10-15', 'My birthday', 'PENDING');

INSERT INTO tag_task VALUES
  (5, 1),
  (1, 2),
  (6, 3),
  (1, 4),
  (6, 5),
  (2, 6),
  (2, 7),
  (3, 7),
  (2, 8),
  (3, 8),
  (2, 9),
  (3, 9),
  (5, 10);
