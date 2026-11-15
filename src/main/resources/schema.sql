DROP TABLE IF EXISTS animal;

CREATE TABLE animal
(
    registration_number VARCHAR(20)   PRIMARY KEY,
    arrival_date        DATE          NOT NULL,
    weight              NUMERIC(7, 2) NOT NULL CHECK (weight > 0),
    origin              VARCHAR(100)  NOT NULL
);

INSERT INTO animal (registration_number, arrival_date, weight, origin) VALUES ('A-1001', '2026-10-01', 112.50, 'Hansen Farm');
INSERT INTO animal (registration_number, arrival_date, weight, origin) VALUES ('A-1002', '2026-10-01', 108.00, 'Nielsen Farm');
INSERT INTO animal (registration_number, arrival_date, weight, origin) VALUES ('A-1003', '2026-10-01', 115.25, 'Hansen Farm');
INSERT INTO animal (registration_number, arrival_date, weight, origin) VALUES ('A-1004', '2026-10-02', 104.75, 'Jensen Farm');
INSERT INTO animal (registration_number, arrival_date, weight, origin) VALUES ('A-1005', '2026-10-02', 110.00, 'Hansen Farm');