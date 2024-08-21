-- INSERT INTO to_do(id, title, description, status) 
-- VALUES
-- (101, 'Cook', 'Cook Adobo', 'PENDING'),
-- (102, 'Exercise', 'Morning run', 'COMPLETED'),
-- (103, 'Read', 'Read a book', 'PENDING');

INSERT INTO to_do (id, title, description, status, created_at, completed_at) 
VALUES
(101, 'Cook', 'Cook Adobo', 'PENDING', CURRENT_DATE, NULL),
(102, 'Exercise', 'Morning run', 'COMPLETED', CURRENT_DATE, DATEADD('DAY', 1, CURRENT_DATE)),
(103, 'Read', 'Read a book', 'PENDING', CURRENT_DATE, NULL);