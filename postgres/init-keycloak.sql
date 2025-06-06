-- Create role if not exists
DO $$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles WHERE rolname = 'keycloak'
   ) THEN
      CREATE ROLE keycloak WITH LOGIN PASSWORD 'keycloak';
   END IF;
END
$$;

-- Create database
-- Only attempt if it doesn't already exist
--CREATE DATABASE "keycloak-ps" OWNER keycloak
--  TEMPLATE template1
--  ENCODING 'UTF8'
--  LC_COLLATE='en_US.utf8'
--  LC_CTYPE='en_US.utf8';
