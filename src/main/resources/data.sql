MERGE INTO mpa as m
USING(VALUES('G'),
      ('PG'),
      ('PG-13'),
      ('R'),
      ('NC-17')
    ) as v(name)
ON m.name=v.name
WHEN MATCHED THEN UPDATE SET m.name=v.name
WHEN NOT MATCHED THEN INSERT (name) VALUES (v.name);

MERGE INTO genre as g
USING(VALUES('Комедия'),
      ('Драма'),
      ('Мультфильм'),
      ('Триллер'),
      ('Документальный'),
      ('Боевик')) as v(name)
ON g.name=v.name
WHEN MATCHED THEN UPDATE SET g.name=v.name
WHEN NOT MATCHED THEN INSERT (name) VALUES(v.name);

