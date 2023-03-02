DROP TABLE sykepengesoknad_yreksskade_status;

CREATE TABLE sykepengesoknad_yrkesskade_status
(
    id                        VARCHAR DEFAULT uuid_generate_v4() PRIMARY KEY,
    sykepengesoknad_uuid_hash VARCHAR NOT NULL UNIQUE,
    yrkesskade_status         VARCHAR NOT NULL,
    sendt                     TIMESTAMP WITH TIME ZONE NOT NULL
);


