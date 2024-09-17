INSERT INTO store (
    id,
    user_id,
    business_registration_number,
    open_date,
    admin_name,
    store_name,
    address,
    detail_address,
    is_closed_today,
    total_seat_usage_minute,
    total_number_of_people_using_store,
    created_by,
    last_modified_by,
    created_at,
    updated_at,
    state
) VALUES (
             1,
             1,                               -- user_id (user 테이블의 외래 키)
             '123-45-67890',                  -- business_registration_number
             '2023-01-01',                    -- open_date
             '관리자',                         -- admin_name
             'My Store',                      -- store_name
             '서울시 강남구',                   -- address
             '역삼동 123-45',                  -- detail_address
             false,                           -- is_closed_today
             0,                               -- total_seat_usage_minute
             0,                               -- total_number_of_people_using_store
             NULL,
             NULL,
             NOW(),
             NOW(),
             1
         );
