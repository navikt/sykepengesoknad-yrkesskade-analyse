CREATE TABLE sykepengesoknad_yreksskade_status
(
    id                        VARCHAR DEFAULT uuid_generate_v4() PRIMARY KEY,
    sykepengesoknad_uuid_hash VARCHAR NOT NULL UNIQUE,
    yrkesskade_status         VARCHAR NOT NULL
);


