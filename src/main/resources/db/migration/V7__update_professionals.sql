CREATE TABLE IF NOT EXISTS disciplines(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid()
);

ALTER TABLE professionals ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE professionals ADD COLUMN biography JSONB DEFAULT '{}'::jsonb;

