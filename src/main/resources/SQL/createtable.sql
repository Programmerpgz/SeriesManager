CREATE TABLE public.users (
    id SERIAL PRIMARY KEY,
    username character varying(100) NOT NULL UNIQUE,
    password character varying(255) NOT NULL,
    name character varying(100) NOT NULL,
    surname character varying(100) NOT NULL,
    email character varying(255) NOT NULL,
    role character varying(50) NOT NULL CHECK (role IN ('ADMINISTRATOR', 'USER'))
);

CREATE TABLE public.director (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    surname character varying(100) NOT NULL,
    year_of_birth integer,
    nationality character varying(100),
    biography text
);

CREATE TABLE public.actor (
    id SERIAL PRIMARY KEY,
    name character varying(100) NOT NULL,
    surname character varying(100) NOT NULL,
    year_of_birth integer,
    nationality character varying(100)
);

CREATE TABLE public.series (
    id SERIAL PRIMARY KEY,
    name character varying(255) NOT NULL,
    number_of_seasons integer DEFAULT 1,
    number_of_episodes integer DEFAULT 0,
    year_of_release integer,
    end_year integer,
    description text,
    poster_path character varying(255),
    director_id integer REFERENCES public.director(id) ON DELETE SET NULL
);

CREATE TABLE public.series_actor (
    series_id integer NOT NULL REFERENCES public.series(id) ON DELETE CASCADE,
    actor_id integer NOT NULL REFERENCES public.actor(id) ON DELETE CASCADE,
    PRIMARY KEY (series_id, actor_id)
);

CREATE TABLE public.series_genre (
    series_id integer NOT NULL REFERENCES public.series(id) ON DELETE CASCADE,
    genre character varying(50) NOT NULL
);

CREATE TABLE public.series_platform (
    series_id integer NOT NULL REFERENCES public.series(id) ON DELETE CASCADE,
    platform character varying(50) NOT NULL
);

CREATE TABLE watchlist (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    series_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_series FOREIGN KEY(series_id) REFERENCES series(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_series UNIQUE(user_id, series_id)
);

INSERT INTO public.users (username, password, name, surname, email, role)
VALUES
('admin', 'admin123', 'System', 'Admin', 'admin@algebra.hr', 'ADMINISTRATOR'),
('user', 'user123', 'System', 'User', 'user@algebra.hr', 'USER');