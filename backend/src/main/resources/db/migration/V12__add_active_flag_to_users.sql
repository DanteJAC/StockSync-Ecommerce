-- V12__add_active_flag_to_users.sql

ALTER TABLE users ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;
