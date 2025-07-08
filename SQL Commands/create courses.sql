INSERT INTO course (
    title, creation_date, duration, language, area, description, link, image, is_active, admin_id
) VALUES
('React Fundamentals',     NOW(), 40, 'ENGLISH', 'FRONTEND', 'Learn React.js basics and components',           'https://example.com/react',       TRUE,  TRUE,  1),
('Java Spring Boot',       NOW(), 50, 'ENGLISH', 'BACKEND',  'Backend development with Spring Boot',          'https://example.com/springboot',  TRUE,  TRUE,  1),
('Docker & Kubernetes',    NOW(), 35, 'ENGLISH', 'INFRASTRUCTURE', 'Container orchestration and deployment',   'https://example.com/docker',      TRUE,  TRUE,  1),
('UI/UX Design Principles', NOW(), 30, 'PORTUGUESE', 'UX_UI', 'User experience and interface design',        'https://example.com/uxui',        TRUE,  TRUE,  1),
('Vue.js Development',     NOW(), 25, 'PORTUGUESE', 'FRONTEND', 'Frontend development with Vue.js',          'https://example.com/vue',         TRUE,  TRUE,  1);