-- Topic 초기 데이터
INSERT INTO topic (id, suggested_topic_id, title, slug, status, thumbnail_image_url, created_at, published_at, end_at)
VALUES (1, NULL, '메시 VS 호날두', 'messi-vs-ronaldo', 'ACTIVE', 'https://i.namu.wiki/i/gVVs5DcyW5Rt16UNmp_2pMLZOcnsZawZun5ZnRymSsULS_Hijt6FbxkuJV5uXS2xNhU60nOXxZZuKn5-qrLJJw.webp',  now(), now(), NULL),
       (2, NULL, '카리나 VS 장원영', 'karina-vs-jang-won-young', 'ACTIVE', 'https://cdn.metakr.co.kr/news/photo/202403/4494_9298_5256.jpg',  now(), now(), NULL),
       (3, NULL, '짬뽕 VS 짜장면', 'jjamppong-vs-jjajang', 'ACTIVE', 'https://i.ytimg.com/vi/xhc5Ds-3ahM/maxresdefault.jpg',  now(), now(), NULL);

-- Choice 초기 데이터
INSERT INTO choice (id, topic_id, name, image_url, vote_count, version)
VALUES (1, 1, '메시', 'https://fifpro.org/media/5chb3dva/lionel-messi_imago1019567000h.jpg?rxy=0.32986930611281567,0.18704579979466449&rnd=133378758718600000', 0, 0),
       (2, 1, '호날두', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQb-NGEQDekk2BwsllLjk4tcIM_BPIzXECdsg&s', 0, 0),
       (3, 2, '카리나', 'https://i.pinimg.com/736x/fa/ee/75/faee75d38f6636b81ad9ceacd9163a45.jpg', 0, 0),
       (4, 2, '장원영', 'https://upload.wikimedia.org/wikipedia/commons/e/ee/2023_MMA_IVE_Wonyoung_1.jpg', 0, 0),
       (5, 2, '설윤', 'https://blog.kakaocdn.net/dn/15c1u/btrBF5rq2s1/uCDT0O1GSpm5WEu8kHzna0/img.jpg', 0, 0),
       (6, 2, '유나', 'https://dispatch.cdnser.be/cms-content/uploads/2023/10/26/34d8a288-ba3a-49ca-91da-767f69d03f5e.jpg', 0, 0),
       (7, 3, '짬뽕', 'https://img.siksinhot.com/article/1520236678174003.jpg', 0, 0),
       (8, 3, '짜장면', 'https://img.siksinhot.com/place/1687310897617109.jpg?c=Y', 0, 0);


