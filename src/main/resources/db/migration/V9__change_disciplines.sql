DROP INDEX IF EXISTS idx_professional_disciplines_professional_id;
DROP INDEX IF EXISTS idx_professional_disciplines_discipline_id;

DROP TABLE IF EXISTS professional_disciplines;
DROP TABLE IF EXISTS disciplines;

CREATE TABLE IF NOT EXISTS disciplines(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name JSONB NOT NULL DEFAULT '{}'::jsonb,
    slug VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

INSERT INTO disciplines (slug, name) VALUES
        ('mma', '{
          "uk": "ММА",
          "pl": "MMA",
          "ru": "ММА",
          "en": "MMA"
       }'::jsonb),
       ('boxing', '{
         "uk": "Бокс",
         "pl": "Boks",
         "ru": "Бокс",
         "en": "Boxing"
       }'::jsonb),
       ('kickboxing', '{
         "uk": "Кікбоксинг",
         "pl": "Kickboxing",
         "ru": "Кикбоксинг",
         "en": "Kickboxing"
       }'::jsonb),
       ('grappling', '{
         "uk": "Греплінг",
         "pl": "Grappling",
         "ru": "Грэпплинг",
         "en": "Grappling"
       }'::jsonb),
       ('muay_thai', '{
         "uk": "Тайський бокс",
         "pl": "Muay Thai",
         "ru": "Тайский бокс",
         "en": "Muay Thai"
       }'::jsonb),
       ('karate', '{
         "uk": "Карате",
         "pl": "Karate",
         "ru": "Карате",
         "en": "Karate"
       }'::jsonb),
       ('judo', '{
         "uk": "Дзюдо",
         "pl": "Judo",
         "ru": "Дзюдо",
         "en": "Judo"
       }'::jsonb),
       ('bjj', '{
         "uk": "Бразильське джиу-джитсу",
         "pl": "Brazylijskie jiu-jitsu",
         "ru": "Бразильское джиу-джитсу",
         "en": "Brazilian Jiu-Jitsu"
       }'::jsonb),
       ('wrestling', '{
         "uk": "Вільна боротьба",
         "pl": "Zapasy",
         "ru": "Вольная борьба",
         "en": "Wrestling"
       }'::jsonb),
       ('taekwondo', '{
         "uk": "Тхеквондо",
         "pl": "Taekwondo",
         "ru": "Тхэквондо",
         "en": "Taekwondo"
       }'::jsonb),
       ('sambo', '{
         "uk": "Самбо",
         "pl": "Sambo",
         "ru": "Самбо",
         "en": "Sambo"
       }'::jsonb),
       ('combat_sambo', '{
         "uk": "Бойове самбо",
         "pl": "Sambo bojowe",
         "ru": "Боевое самбо",
         "en": "Combat Sambo"
       }'::jsonb)
ON CONFLICT (slug) DO NOTHING;

CREATE TABLE IF NOT EXISTS professional_disciplines
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    professional_id UUID NOT NULL REFERENCES professionals(id) ON DELETE CASCADE,
    discipline_id   UUID NOT NULL REFERENCES disciplines(id) ON DELETE CASCADE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE (professional_id, discipline_id)
);

CREATE INDEX IF NOT EXISTS idx_professional_disciplines_professional_id ON professional_disciplines (professional_id);
CREATE INDEX IF NOT EXISTS idx_professional_disciplines_discipline_id ON professional_disciplines (discipline_id);
