INSERT INTO custom_utilization_field (
    id, store_id, title, type, content_guide, created_by, last_modified_by, created_at, updated_at, state
) VALUES (
             1,
             1, -- store_id: 연결된 Store 엔터티의 ID
             '이용 목적', -- title: 커스텀 필드 제목
             'TEXT', -- type: CustomUtilizationFieldType 열거형 값 (예: 'TYPE1' 또는 'TYPE2')
             'This is a content guide.', -- contentGuide: 가이드 내용
             NULL, -- createdBy: 생성자
             NULL, -- lastModifiedBy: 마지막 수정자
             NOW(), -- createdAt: 생성일시 (현재 시간)
             NOW(), -- updatedAt: 수정일시 (현재 시간)
             1
         );
