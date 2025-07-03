INSERT INTO useraccount (
    email, password, avatar, name, surname,
    is_admin, is_deleted, is_manager, office, phone,
    birthdate, street, postalcode, municipality, biography,
    account_state, role, creation_date, secretKey,
    onlinestatus, lastseen, manager_id
)
VALUES
('alice.mendes@example.com', 'hash1', FALSE, 'Alice', 'Mendes', FALSE, FALSE, TRUE, 'LISBON', '911111111', '1990-05-20', 'Rua A 123', '1000-001', 'Lisboa', 'UX lead.', 'COMPLETE', 'UX_UI_DESIGNER', NOW(), 'key001', TRUE, NOW() - INTERVAL '5 minutes', 8),
('bruno.pereira@example.com', 'hash2', FALSE, 'Bruno', 'Pereira', TRUE, FALSE, FALSE, 'COIMBRA', '922222222', '1988-08-12', 'Rua B 456', '3000-112', 'Coimbra', 'Backend senior.', 'COMPLETE', 'BACKEND_DEVELOPER', NOW(), 'key002', FALSE, NOW() - INTERVAL '2 days', 8),
('carla.silva@example.com', 'hash3', FALSE, 'Carla', 'Silva', FALSE, FALSE, FALSE, 'OPORTO', '933333333', '1992-03-15', 'Rua C 789', '4000-333', 'Porto', 'PM from FMCG background.', 'INCOMPLETE', 'PRODUCT_MANAGER', NOW(), 'key003', FALSE, NOW() - INTERVAL '10 days', 15),
('daniel.oliveira@example.com', 'hash4', FALSE, 'Daniel', 'Oliveira', FALSE, FALSE, TRUE, 'VISEU', '944444444', '1985-11-10', 'Rua D 321', '3500-004', 'Viseu', 'Security ops.', 'COMPLETE', 'SECURITY_ANALYST', NOW(), 'key004', TRUE, NOW() - INTERVAL '30 minutes', 8),
('eva.ferreira@example.com', 'hash5', FALSE, 'Eva', 'Ferreira', TRUE, FALSE, FALSE, 'BOSTON', '955555555', '1991-07-07', 'Beacon St 45', '02108', 'Boston', 'Global HR.', 'COMPLETE', 'HR_MANAGER', NOW(), 'key005', TRUE, NOW() - INTERVAL '1 minute', 23),

('filipe.gomes@example.com', 'hash6', FALSE, 'Filipe', 'Gomes', FALSE, TRUE, FALSE, 'MUNICH', '966666666', '1983-02-28', 'Schillerstraße 3', '80336', 'Munich', 'Legacy sys admin.', 'COMPLETE', 'SYSTEM_ADMINISTRATOR', NOW(), 'key006', FALSE, NOW() - INTERVAL '3 days', 8),
('giovana.costa@example.com', 'hash7', FALSE, 'Giovana', 'Costa', FALSE, FALSE, FALSE, 'SOUTHAMPTON', '977777777', '1995-09-19', 'Dockside Ave 101', 'SO14 3EL', 'Southampton', NULL, 'INCOMPLETE', 'FRONTEND_DEVELOPER', NOW(), 'key007', FALSE, NOW() - INTERVAL '15 days', 10),
('henrique.sousa@example.com', 'hash8', FALSE, 'Henrique', 'Sousa', TRUE, FALSE, TRUE, 'LISBON', '988888888', '1980-01-01', 'Rua H 753', '1000-008', 'Lisboa', 'CTO', 'COMPLETE', 'CTO', NOW(), 'key008', TRUE, NOW() - INTERVAL '10 minutes', NULL),
('ines.martins@example.com', 'hash9', FALSE, 'Inês', 'Martins', FALSE, FALSE, FALSE, 'COIMBRA', '999999999', '1993-12-30', 'Rua I 852', '3000-009', 'Coimbra', NULL, 'COMPLETE', 'QA_ENGINEER', NOW(), 'key009', FALSE, NOW() - INTERVAL '1 day', 8),
('joao.moreira@example.com', 'hash10', FALSE, 'João', 'Moreira', FALSE, FALSE, TRUE, 'OPORTO', '900000000', '1987-04-22', 'Rua J 951', '4000-010', 'Porto', 'Full-stack wizard.', 'COMPLETE', 'FULL_STACK_DEVELOPER', NOW(), 'key010', TRUE, NOW() - INTERVAL '2 hours', 8),

('karen.pinto@example.com', 'hash11', FALSE, 'Karen', 'Pinto', FALSE, FALSE, FALSE, 'LISBON', '901000001', '1991-06-14', 'Rua K', '1000-011', 'Lisboa', 'Junior dev.', 'INCOMPLETE', 'SOFTWARE_ENGINEER', NOW(), 'key011', FALSE, NOW() - INTERVAL '20 days', 8),
('luis.teixeira@example.com', 'hash12', FALSE, 'Luís', 'Teixeira', FALSE, FALSE, FALSE, 'COIMBRA', '902000002', '1986-01-20', 'Rua L', '3000-012', 'Coimbra', 'DevOps CI/CD.', 'COMPLETE', 'DEVOPS_ENGINEER', NOW(), 'key012', TRUE, NOW() - INTERVAL '4 minutes', 8),
('maria.ramos@example.com', 'hash13', FALSE, 'Maria', 'Ramos', FALSE, FALSE, FALSE, 'BOSTON', '903000003', '1990-03-08', 'Park Ave', '02118', 'Boston', 'BA in fintech.', 'COMPLETE', 'BUSINESS_ANALYST', NOW(), 'key013', FALSE, NOW() - INTERVAL '8 days', 23),
('nuno.alves@example.com', 'hash14', FALSE, 'Nuno', 'Alves', FALSE, FALSE, FALSE, 'MUNICH', '904000004', '1981-07-17', 'Karlstraße', '80333', 'Munich', 'SRE engineer.', 'COMPLETE', 'DEVOPS_ENGINEER', NOW(), 'key014', TRUE, NOW() - INTERVAL '50 minutes', 8),
('olga.machado@example.com', 'hash15', FALSE, 'Olga', 'Machado', TRUE, FALSE, FALSE, 'LISBON', '905000005', '1975-10-29', 'Rua O', '1000-013', 'Lisboa', 'Founder.', 'COMPLETE', 'CEO', NOW(), 'key015', TRUE, NOW() - INTERVAL '5 minutes', NULL),

('pedro.carvalho@example.com', 'hash16', FALSE, 'Pedro', 'Carvalho', FALSE, FALSE, FALSE, 'SOUTHAMPTON', '906000006', '1989-02-11', 'Elm St', 'SO15 1AL', 'Southampton', 'HR support.', 'INCOMPLETE', 'HR_SPECIALIST', NOW(), 'key016', FALSE, NOW() - INTERVAL '12 days', 23),
('quiteria.vieira@example.com', 'hash17', FALSE, 'Quitéria', 'Vieira', FALSE, FALSE, TRUE, 'COIMBRA', '907000007', '1994-05-03', 'Rua Q', '3000-014', 'Coimbra', 'Recruitment lead.', 'COMPLETE', 'RECRUITER', NOW(), 'key017', TRUE, NOW() - INTERVAL '2 minutes', 23),
('ricardo.neves@example.com', 'hash18', FALSE, 'Ricardo', 'Neves', FALSE, FALSE, FALSE, 'BOSTON', '908000008', '1984-08-15', 'Summer St', '02111', 'Boston', 'Data wrangler.', 'COMPLETE', 'DATA_SCIENTIST', NOW(), 'key018', FALSE, NOW() - INTERVAL '6 days', 23),
('sara.fonseca@example.com', 'hash19', FALSE, 'Sara', 'Fonseca', FALSE, TRUE, FALSE, 'VISEU', '909000009', '1993-10-01', 'Rua S', '3500-015', 'Viseu', 'On sabbatical.', 'INCOMPLETE', 'SOFTWARE_ENGINEER', NOW(), 'key019', FALSE, NOW() - INTERVAL '25 days', 8),
('tomas.gaspar@example.com', 'hash20', FALSE, 'Tomás', 'Gaspar', FALSE, FALSE, FALSE, 'OPORTO', '910000010', '1986-09-09', 'Rua T', '4000-016', 'Porto', 'Tech mentor.', 'COMPLETE', 'TECH_LEAD', NOW(), 'key020', TRUE, NOW() - INTERVAL '15 minutes', 8),

('ursula.reis@example.com', 'hash21', FALSE, 'Úrsula', 'Reis', FALSE, FALSE, FALSE, 'COIMBRA', '911000011', '1985-03-27', 'Rua U', '3000-017', 'Coimbra', NULL, 'COMPLETE', 'QA_ENGINEER', NOW(), 'key021', FALSE, NOW() - INTERVAL '18 days', 17),
('valter.sousa@example.com', 'hash22', FALSE, 'Valter', 'Sousa', FALSE, FALSE, FALSE, 'MUNICH', '912000012', '1982-12-12', 'Goethestraße', '80336', 'Munich', 'Network security.', 'COMPLETE', 'SECURITY_ANALYST', NOW(), 'key022', TRUE, NOW() - INTERVAL '3 minutes', 8),
('wanda.freitas@example.com', 'hash23', FALSE, 'Wanda', 'Freitas', TRUE, FALSE, TRUE, 'LISBON', '913000013', '1990-06-05', 'Rua W', '1000-018', 'Lisboa', 'Head of HR.', 'COMPLETE', 'HR_MANAGER', NOW(), 'key023', TRUE, NOW() - INTERVAL '1 minute', 15);
