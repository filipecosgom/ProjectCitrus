INSERT INTO course (
    title, creation_date, duration, language, area, description, link, image, is_active, admin_id
) VALUES
('Java Basics',        NOW(), 40, 'ENGLISH', 'COMPUTER_SCIENCE', 'Introductory Java course',        'https://example.com/java',        TRUE,  TRUE,  1),
('Python for Data',    NOW(), 30, 'ENGLISH', 'DATA_SCIENCE',     'Python for data analysis',        'https://example.com/python',      TRUE,  TRUE,  1),
('Web Dev Essentials', NOW(), 25, 'PORTUGUESE', 'WEB_DEVELOPMENT', 'Web development fundamentals',   'https://example.com/webdev',      TRUE,  TRUE,  1),
('AI Fundamentals',    NOW(), 50, 'ENGLISH', 'ARTIFICIAL_INTELLIGENCE', 'Basics of AI',              'https://example.com/ai',          TRUE,  TRUE,  1),
('Databases 101',      NOW(), 35, 'PORTUGUESE', 'DATABASES',      'Introduction to databases',      'https://example.com/db',          TRUE,  TRUE,  1);