INSERT INTO store_table (
    id,
    store_id,
    store_space_id,
    id_by_web,
    tablex,
    tabley,
    width,
    height,
    is_in_use,
    created_by,
    last_modified_by,
    created_at,
    updated_at,
    state
) VALUES (
             1,
             1,                          -- store_id (store 테이블의 외래 키)
             1,                          -- store_space_id (store_space 테이블의 외래 키)
             'table-01',                 -- id_by_web (테이블의 웹 관리용 ID)
             100,                        -- table_x (테이블의 X 좌표)
             200,                        -- table_y (테이블의 Y 좌표)
             150,                        -- width (테이블의 가로 길이)
             75,                         -- height (테이블의 세로 길이)
             false,                      -- is_in_use (테이블 사용 여부)
             NULL,                       -- created_by (NULL로 설정)
             NULL,                       -- last_modified_by (NULL로 설정)
             NOW(),                      -- created_at (현재 시간으로 설정)
             NOW(),                      -- updated_at (현재 시간으로 설정)
             1                    -- state (ACTIVE 또는 INACTIVE)
         );
