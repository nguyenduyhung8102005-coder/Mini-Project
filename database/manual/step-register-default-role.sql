-- Run once before using POST /auth/register if ROLE_USER does not exist.
INSERT INTO tbl_role (name, description)
SELECT 'ROLE_USER', 'Default role for registered users'
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_role
    WHERE name = 'ROLE_USER'
);
