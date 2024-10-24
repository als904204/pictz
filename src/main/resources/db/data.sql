-- Topic 초기 데이터
INSERT INTO topic (id, suggested_topic_id, title, slug, status, thumbnail_image_url, created_at, published_at, end_at)
VALUES (1, NULL, '메시 VS 호날두', 'messi-vs-ronaldo', 'ACTIVE', 'https://i.namu.wiki/i/gVVs5DcyW5Rt16UNmp_2pMLZOcnsZawZun5ZnRymSsULS_Hijt6FbxkuJV5uXS2xNhU60nOXxZZuKn5-qrLJJw.webp',  now(), now(), NULL),
       (2, NULL, '카리나 VS 장원영', 'karina-vs-jang-won-young', 'ACTIVE', 'https://cdn.metakr.co.kr/news/photo/202403/4494_9298_5256.jpg',  now(), now(), NULL),
       (3, NULL, '짬뽕v VS 짜장면', 'jjamppong-vs-jjajang', 'ACTIVE', 'https://i.ytimg.com/vi/xhc5Ds-3ahM/maxresdefault.jpg',  now(), now(), NULL);

-- Choice 초기 데이터
INSERT INTO choice (id, topic_id, name, image_url, vote_count)
VALUES (1, 1, '메시', 'url_to_messi_image', 0),
       (2, 1, '호날두', 'url_to_ronaldo_image', 0),
       (3, 2, '카리나', 'url_to_karina_image', 0),
       (4, 2, '장원영', 'url_to_jang_won_young_image', 0),
       (5, 3, '짬뽕', 'url_to_jjamppong_image', 0),
       (6, 3, '짜장면', 'url_to_jjajang_image', 0);


