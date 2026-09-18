CREATE TABLE IF NOT EXISTS levels (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug       VARCHAR(64) NOT NULL UNIQUE,
    name       JSONB NOT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS age_groups (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug       VARCHAR(64) NOT NULL UNIQUE,
    name       JSONB NOT NULL,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS class_groups (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    discipline_id      UUID NOT NULL REFERENCES disciplines(id) ON DELETE RESTRICT,
    level_id           UUID NOT NULL REFERENCES levels(id) ON DELETE RESTRICT,
    age_group_id       UUID NOT NULL REFERENCES age_groups(id) ON DELETE RESTRICT,
    default_trainer_id UUID NOT NULL REFERENCES professionals(id) ON DELETE RESTRICT,
    capacity           INTEGER NOT NULL CHECK (capacity > 0),
    name               JSONB NOT NULL,
    description        JSONB,
    is_active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO levels (slug, name) VALUES
    ('beginner', '{
      "uk": "Початківець",
      "pl": "Początkujący",
      "ru": "Начинающий",
      "en": "Beginner"
    }'::jsonb),
    ('intermediate', '{
      "uk": "Середній",
      "pl": "Średniozaawansowany",
      "ru": "Средний",
      "en": "Intermediate"
    }'::jsonb),
    ('advanced', '{
      "uk": "Просунутий",
      "pl": "Zaawansowany",
      "ru": "Продвинутый",
      "en": "Advanced"
    }'::jsonb)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO age_groups (slug, name) VALUES
    ('kids', '{
      "uk": "Діти",
      "pl": "Dzieci",
      "ru": "Дети",
      "en": "Kids"
    }'::jsonb),
    ('teens', '{
      "uk": "Підлітки",
      "pl": "Nastolatki",
      "ru": "Подростки",
      "en": "Teens"
    }'::jsonb),
    ('adults', '{
      "uk": "Дорослі",
      "pl": "Dorośli",
      "ru": "Взрослые",
      "en": "Adults"
    }'::jsonb)
ON CONFLICT (slug) DO NOTHING;
