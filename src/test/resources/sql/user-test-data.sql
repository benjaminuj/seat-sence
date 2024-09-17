INSERT INTO user (
    id,
    email,
    password,
    role,
    name,
    birth_date,
    nickname,
    sex,
    consent_to_marketing,
    consent_to_terms_of_user,
    created_by,
    last_modified_by,
    created_at,
    updated_at,
    state
) VALUES (
             1,
             'gildong@naver.com',           -- email
             'gildong!',              -- password
             'USER',                     -- role (enum value, e.g. ADMIN, USER)
             '홍길동',                   -- name
             '1990-01-01',               -- birthDate
             '길동이',                   -- nickname
             'F',
             true,
             true,
             NULL,
             NULL,
             NOW(),
             NOW(),
             1
         );

INSERT INTO user (
    id,
    email,
    password,
    role,
    name,
    birth_date,
    nickname,
    sex,
    consent_to_marketing,
    consent_to_terms_of_user,
    created_by,
    last_modified_by,
    created_at,
    updated_at,
    state
) VALUES (
             2,
             'minji@naver.com',           -- email
             'minji!',              -- password
             'ADMIN',                     -- role (enum value, e.g. ADMIN, USER)
             '김민지',                   -- name
             '2000-01-01',               -- birthDate
             '민찌',                   -- nickname
             'M',
             true,
             true,
             NULL,
             NULL,
             NOW(),
             NOW(),
             1
         );

