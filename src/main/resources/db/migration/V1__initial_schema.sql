CREATE TABLE firm_snapshots
(
    step_number INTEGER          NOT NULL,
    firm_id     UUID             NOT NULL,

    cash        NUMERIC(20, 2)   NOT NULL,

    inventory   INTEGER          NOT NULL,
    price       NUMERIC(20, 2)   NOT NULL,

    employees   INTEGER          NOT NULL,
    wage        NUMERIC(20, 2)   NOT NULL,

    production  INTEGER          NOT NULL,
    sales       INTEGER          NOT NULL,

    tech_factor DOUBLE PRECISION NOT NULL,

    PRIMARY KEY (step_number, firm_id)
);

CREATE INDEX idx_firm_snapshots_step ON firm_snapshots (step_number);