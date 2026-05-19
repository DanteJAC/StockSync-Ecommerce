-- Renombrar la columna vieja a la nueva convención
ALTER TABLE warehouses RENAME COLUMN date TO created_at;

-- Cambiar el tipo de dato para que soporte horas y minutos (LocalDateTime)
ALTER TABLE warehouses ALTER COLUMN created_at TYPE TIMESTAMP;