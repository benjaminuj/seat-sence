INSERT INTO store_space (
    id,
    store_id,
    name,
    height,
    reservation_unit,
    created_by,
    last_modified_by,
    created_at,
    updated_at,
    state
) VALUES (
             1,
             1,                         -- store_id (store 테이블의 외래 키)
             'Main Space',              -- name (공간의 이름)
             300,                       -- height (공간의 높이)
             'SPACE',                   -- reservation_unit (예약 단위: SPACE 또는 SEAT)
             NULL,                      -- created_by (NULL로 설정)
             NULL,                      -- last_modified_by (NULL로 설정)
             NOW(),
             NOW(),
             1
         );
