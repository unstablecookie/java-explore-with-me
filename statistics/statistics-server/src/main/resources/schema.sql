DROP TABLE IF EXISTS endpointhits;

CREATE TABLE endpointhits (
  id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  app varchar NOT NULL,
  uri varchar NOT NULL,
  ip varchar NOT NULL,
  timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);