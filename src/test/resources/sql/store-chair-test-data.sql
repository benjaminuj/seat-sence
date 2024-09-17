INSERT INTO store_chair (
    id,
    id_by_web,
    manage_id,
    store_id,
    store_space_id,
    chairx,
    chairy,
    is_in_use,
    created_by,
    last_modified_by,
    created_at,
    updated_at,
    state
) VALUES (
             1,                      -- id
             'chair001',                    -- id_by_web (의자의 웹 관리용 id)
             1,                             -- manage_id (의자 관리용 id)
             1,                             -- store_id (store 테이블의 외래 키)
             1,                             -- store_space_id (store_space 테이블의 외래 키)
             10,                            -- chair_x (의자의 x 좌표)
             20,                            -- chair_y (의자의 y 좌표)
             false,                         -- is_in_use (의자의 사용 여부)
             NULL,                          -- created_by (NULL로 설정)
             NULL,                          -- last_modified_by (NULL로 설정)
             NOW(),
             NOW(),
             1
         );
