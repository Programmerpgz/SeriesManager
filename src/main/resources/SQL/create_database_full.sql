--
-- PostgreSQL database dump
--

\restrict 3hFEmCsOGItUdIrhGPQOHUus6ybRdGYNDVFjnTUt0y1F5XUa2598K6SFxabwpna

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: check_username_exists(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_username_exists(p_username character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN (SELECT COUNT(*)::INT FROM users WHERE username = p_username);
END; $$;


ALTER FUNCTION public.check_username_exists(p_username character varying) OWNER TO postgres;

--
-- Name: count_user_occurrences(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.count_user_occurrences(p_username character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN (SELECT COUNT(*)::INT FROM users WHERE username = p_username);
END; $$;


ALTER FUNCTION public.count_user_occurrences(p_username character varying) OWNER TO postgres;

--
-- Name: create_series(character varying, text, integer, integer, character varying, integer, integer, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.create_series(p_name character varying, p_desc text, p_seasons integer, p_episodes integer, p_path character varying, p_year integer, p_end_year integer, p_genre character varying, p_platform character varying, p_dir_name character varying, p_dir_surname character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    new_id integer;
    d_id integer;
BEGIN
    -- 1. Pronađi ili umetni direktora
    SELECT id INTO d_id FROM director 
    WHERE name = p_dir_name AND surname = p_dir_surname;
    
    IF d_id IS NULL THEN
        INSERT INTO director (name, surname) VALUES (p_dir_name, p_dir_surname) 
        RETURNING id INTO d_id;
    END IF;

    -- 2. Umetni seriju
    INSERT INTO series (
        name, description, number_of_seasons, number_of_episodes, 
        poster_path, year_of_release, end_year, genre, platform, director_id
    )
    VALUES (
        p_name, p_desc, p_seasons, p_episodes, 
        p_path, p_year, p_end_year, p_genre, p_platform, d_id
    )
    RETURNING id INTO new_id;

    RETURN new_id;
END;
$$;


ALTER FUNCTION public.create_series(p_name character varying, p_desc text, p_seasons integer, p_episodes integer, p_path character varying, p_year integer, p_end_year integer, p_genre character varying, p_platform character varying, p_dir_name character varying, p_dir_surname character varying) OWNER TO postgres;

--
-- Name: create_series_actor(integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.create_series_actor(IN p_series_id integer, IN p_actor_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Provjeravamo postoji li već ta veza da ne bude duplikata
    IF NOT EXISTS (
        SELECT 1 FROM public.series_actor 
        WHERE series_id = p_series_id AND actor_id = p_actor_id
    ) THEN
        INSERT INTO public.series_actor (series_id, actor_id)
        VALUES (p_series_id, p_actor_id);
    END IF;
END;
$$;


ALTER PROCEDURE public.create_series_actor(IN p_series_id integer, IN p_actor_id integer) OWNER TO postgres;

--
-- Name: create_series_actor(integer, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.create_series_actor(IN p_series_id integer, IN p_actor_name character varying, IN p_actor_surname character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_actor_id INTEGER;
BEGIN
    -- 1. Nađi ili kreiraj glumca
    SELECT id INTO v_actor_id FROM public.actor 
    WHERE name = p_actor_name AND surname = p_actor_surname LIMIT 1;

    IF v_actor_id IS NULL THEN
        INSERT INTO public.actor (name, surname) 
        VALUES (p_actor_name, p_actor_surname) 
        RETURNING id INTO v_actor_id;
    END IF;

    -- 2. Poveži sa serijom (ignore ako već postoji)
    INSERT INTO public.series_actor (series_id, actor_id)
    VALUES (p_series_id, v_actor_id)
    ON CONFLICT DO NOTHING; 
END;
$$;


ALTER PROCEDURE public.create_series_actor(IN p_series_id integer, IN p_actor_name character varying, IN p_actor_surname character varying) OWNER TO postgres;

--
-- Name: delete_actor(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_actor(IN p_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM actor WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.delete_actor(IN p_id integer) OWNER TO postgres;

--
-- Name: delete_all_directors(); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_all_directors()
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM director;
END;
$$;


ALTER PROCEDURE public.delete_all_directors() OWNER TO postgres;

--
-- Name: delete_all_series(); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_all_series()
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM series;
END;
$$;


ALTER PROCEDURE public.delete_all_series() OWNER TO postgres;

--
-- Name: delete_all_users(); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_all_users()
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM users;
END; $$;


ALTER PROCEDURE public.delete_all_users() OWNER TO postgres;

--
-- Name: delete_director(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_director(IN p_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE series SET director_id = NULL WHERE director_id = p_id;
    DELETE FROM director WHERE id = p_id;
END; $$;


ALTER PROCEDURE public.delete_director(IN p_id integer) OWNER TO postgres;

--
-- Name: delete_series(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_series(IN p_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM series WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.delete_series(IN p_id integer) OWNER TO postgres;

--
-- Name: delete_series_actors(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_series_actors(IN p_sid integer)
    LANGUAGE plpgsql
    AS $$ BEGIN DELETE FROM series_actor WHERE series_id = p_sid; END; $$;


ALTER PROCEDURE public.delete_series_actors(IN p_sid integer) OWNER TO postgres;

--
-- Name: delete_series_genres(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_series_genres(IN p_sid integer)
    LANGUAGE plpgsql
    AS $$ BEGIN DELETE FROM series_genre WHERE series_id = p_sid; END; $$;


ALTER PROCEDURE public.delete_series_genres(IN p_sid integer) OWNER TO postgres;

--
-- Name: delete_series_platforms(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_series_platforms(IN p_sid integer)
    LANGUAGE plpgsql
    AS $$ BEGIN DELETE FROM series_platform WHERE series_id = p_sid; END; $$;


ALTER PROCEDURE public.delete_series_platforms(IN p_sid integer) OWNER TO postgres;

--
-- Name: delete_user(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_user(IN p_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM users WHERE id = p_id;
END; $$;


ALTER PROCEDURE public.delete_user(IN p_id integer) OWNER TO postgres;

--
-- Name: delete_watchlist_item(integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_watchlist_item(IN p_user_id integer, IN p_series_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM watchlist 
    WHERE user_id = p_user_id AND series_id = p_series_id;
END; $$;


ALTER PROCEDURE public.delete_watchlist_item(IN p_user_id integer, IN p_series_id integer) OWNER TO postgres;

--
-- Name: get_actor_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_actor_by_id(p_id integer) RETURNS TABLE(id integer, name character varying, surname character varying, year_of_birth integer, nationality character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT a.id, a.name, a.surname, a.year_of_birth, a.nationality
    FROM actor a 
    WHERE a.id = p_id;
END; 
$$;


ALTER FUNCTION public.get_actor_by_id(p_id integer) OWNER TO postgres;

--
-- Name: get_actors_by_nationality(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_actors_by_nationality(p_nat character varying) RETURNS TABLE(id integer, name character varying, surname character varying, year_of_birth integer, nationality character varying, image_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT a.id, a.name, a.surname, a.year_of_birth, a.nationality, a.image_path 
    FROM actor a WHERE LOWER(a.nationality) = LOWER(p_nat);
END;
$$;


ALTER FUNCTION public.get_actors_by_nationality(p_nat character varying) OWNER TO postgres;

--
-- Name: get_actors_by_series(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_actors_by_series(p_sid integer) RETURNS TABLE(id integer, name character varying, nationality character varying, year_of_birth integer, surname character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT a.id, a.name, a.nationality, a.year_of_birth, a.surname 
    FROM actor a JOIN series_actor sa ON a.id = sa.actor_id WHERE sa.series_id = p_sid;
END; $$;


ALTER FUNCTION public.get_actors_by_series(p_sid integer) OWNER TO postgres;

--
-- Name: get_all_actors(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_actors() RETURNS TABLE(id integer, name character varying, surname character varying, year_of_birth integer, nationality character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT a.id, a.name, a.surname, a.year_of_birth, a.nationality 
    FROM actor a ORDER BY a.name;
END; 
$$;


ALTER FUNCTION public.get_all_actors() OWNER TO postgres;

--
-- Name: get_all_directors(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_directors() RETURNS TABLE(id integer, name character varying, surname character varying, year_of_birth integer, nationality character varying, biography text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT d.id, d.name, d.surname, d.year_of_birth, d.nationality, d.biography 
    FROM director d ORDER BY d.name;
END; 
$$;


ALTER FUNCTION public.get_all_directors() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: series; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.series (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    number_of_seasons integer DEFAULT 1,
    number_of_episodes integer DEFAULT 0,
    year_of_release integer,
    end_year integer,
    description text,
    poster_path character varying(255),
    director_id integer,
    genre character varying(255),
    platform character varying(255)
);


ALTER TABLE public.series OWNER TO postgres;

--
-- Name: get_all_series(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_series() RETURNS SETOF public.series
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM public.series ORDER BY name;
END;
$$;


ALTER FUNCTION public.get_all_series() OWNER TO postgres;

--
-- Name: get_all_users(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_users() RETURNS TABLE(id integer, username character varying, password character varying, surname character varying, name character varying, email character varying, role character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM users;
END; $$;


ALTER FUNCTION public.get_all_users() OWNER TO postgres;

--
-- Name: get_director_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_director_by_id(p_id integer) RETURNS TABLE(id integer, name character varying, surname character varying, year_of_birth integer, nationality character varying, biography text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT d.id, d.name, d.surname, d.year_of_birth, d.nationality, d.biography
    FROM director d 
    WHERE d.id = p_id;
END; 
$$;


ALTER FUNCTION public.get_director_by_id(p_id integer) OWNER TO postgres;

--
-- Name: get_emitting_series(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_emitting_series() RETURNS SETOF public.series
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT * FROM public.series s 
    WHERE s.end_year IS NULL; -- Pretpostavljam da ti je ovo logika za "still emitting"
END;
$$;


ALTER FUNCTION public.get_emitting_series() OWNER TO postgres;

--
-- Name: get_filtered_logs(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_filtered_logs(p_level_filter character varying) RETURNS TABLE(id integer, ts timestamp without time zone, uname character varying, act text, lvl character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT user_logs.id, user_logs.timestamp, user_logs.username, user_logs.action, user_logs.level
    FROM user_logs
    WHERE (p_level_filter = 'ALL' OR user_logs.level = p_level_filter)
    ORDER BY user_logs.timestamp DESC;
END;
$$;


ALTER FUNCTION public.get_filtered_logs(p_level_filter character varying) OWNER TO postgres;

--
-- Name: get_genre_statistics(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_genre_statistics() RETURNS TABLE(genre character varying, total integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT sg.genre, COUNT(*)::INT FROM series_genre sg GROUP BY sg.genre;
END; $$;


ALTER FUNCTION public.get_genre_statistics() OWNER TO postgres;

--
-- Name: get_genres_by_series(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_genres_by_series(p_sid integer) RETURNS TABLE(genre character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT sg.genre FROM series_genre sg WHERE sg.series_id = p_sid;
END; $$;


ALTER FUNCTION public.get_genres_by_series(p_sid integer) OWNER TO postgres;

--
-- Name: get_or_create_director(text, text, integer, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_or_create_director(p_name text, p_surname text, p_year_of_birth integer, p_nationality text, p_biography text) RETURNS TABLE(id integer, name text, surname text, year_of_birth integer, nationality text, biography text)
    LANGUAGE plpgsql
    AS $$
BEGIN

    BEGIN
        INSERT INTO director(
            name,
            surname,
            year_of_birth,
            nationality,
            biography
        )
        VALUES (
            p_name,
            p_surname,
            p_year_of_birth,
            p_nationality,
            p_biography
        );

    EXCEPTION
        WHEN unique_violation THEN
            NULL;
    END;

    RETURN QUERY
SELECT
    d.id,
    d.name::text,
    d.surname::text,
    d.year_of_birth,
    d.nationality::text,
    d.biography::text
FROM director d
WHERE lower(d.name) = lower(p_name)
  AND lower(d.surname) = lower(p_surname)
LIMIT 1;

END;
$$;


ALTER FUNCTION public.get_or_create_director(p_name text, p_surname text, p_year_of_birth integer, p_nationality text, p_biography text) OWNER TO postgres;

--
-- Name: get_platforms_by_series(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_platforms_by_series(p_sid integer) RETURNS TABLE(platform character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT sp.platform FROM series_platform sp WHERE sp.series_id = p_sid;
END; $$;


ALTER FUNCTION public.get_platforms_by_series(p_sid integer) OWNER TO postgres;

--
-- Name: get_series_by_director(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_series_by_director(p_did integer) RETURNS TABLE(id integer, name character varying, number_of_seasons integer, number_of_episodes integer, year_of_release integer, end_year integer, description text, poster_path character varying, director_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM series s WHERE s.director_id = p_did;
END; $$;


ALTER FUNCTION public.get_series_by_director(p_did integer) OWNER TO postgres;

--
-- Name: get_series_by_genre(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_series_by_genre(p_genre text) RETURNS SETOF public.series
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT * FROM public.series 
    WHERE genre ILIKE ('%' || p_genre || '%');
END;
$$;


ALTER FUNCTION public.get_series_by_genre(p_genre text) OWNER TO postgres;

--
-- Name: get_series_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_series_by_id(p_id integer) RETURNS SETOF public.series
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM public.series WHERE id = p_id;
END;
$$;


ALTER FUNCTION public.get_series_by_id(p_id integer) OWNER TO postgres;

--
-- Name: get_series_by_platform(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_series_by_platform(p_platform text) RETURNS SETOF public.series
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT * FROM public.series 
    WHERE platform ILIKE ('%' || p_platform || '%');
END;
$$;


ALTER FUNCTION public.get_series_by_platform(p_platform text) OWNER TO postgres;

--
-- Name: get_user_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_user_by_id(p_id integer) RETURNS TABLE(id integer, username character varying, password character varying, surname character varying, name character varying, email character varying, role character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM users u WHERE u.id = p_id;
END; $$;


ALTER FUNCTION public.get_user_by_id(p_id integer) OWNER TO postgres;

--
-- Name: get_user_by_username(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_user_by_username(p_username character varying) RETURNS TABLE(id integer, username character varying, password character varying, surname character varying, name character varying, email character varying, role character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM users u WHERE u.username = p_username;
END; $$;


ALTER FUNCTION public.get_user_by_username(p_username character varying) OWNER TO postgres;

--
-- Name: get_user_watchlist(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_user_watchlist(p_user_id integer) RETURNS TABLE(idseries integer, seriesname character varying, seriesdescription text, numberofepisodes integer, numberofseasons integer, yearofrelease integer, endyear integer, picturepath character varying, genres text, platforms text, directorname character varying, actors text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.id AS idSeries, 
        s.name AS seriesName, 
        s.description AS seriesDescription, 
        s.number_of_episodes AS numberOfEpisodes, 
        s.number_of_seasons AS numberOfSeasons, 
        s.year_of_release AS yearOfRelease, 
        s.end_year AS endYear, 
        s.poster_path AS picturePath,
        -- Dohvaćamo žanrove iz series_genre tablice
		COALESCE((SELECT STRING_AGG(sg.genre, ', ') FROM series_genre sg WHERE sg.series_id = s.id), 'N/A'),
        -- Dohvaćamo platforme iz series_platform tablice
        COALESCE((SELECT STRING_AGG(sp.platform, ', ') FROM series_platform sp WHERE sp.series_id = s.id), 'N/A'),
        -- Direktor
		COALESCE((
            SELECT (d.name || ' ' || d.surname)::VARCHAR 
            FROM director d 
            WHERE d.id = s.director_id
        ), 'N/A')::VARCHAR,
		COALESCE((
            SELECT STRING_AGG(act.name || ' ' || act.surname, ', ') 
            FROM series_actor sa 
            JOIN actor act ON sa.actor_id = act.id 
            WHERE sa.series_id = s.id
        ), 'N/A')
    FROM series s
    JOIN watchlist w ON s.id = w.series_id
    WHERE w.user_id = p_user_id;
END; $$;


ALTER FUNCTION public.get_user_watchlist(p_user_id integer) OWNER TO postgres;

--
-- Name: get_users_by_role(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_users_by_role(p_role character varying) RETURNS TABLE(id integer, username character varying, password character varying, surname character varying, name character varying, email character varying, role character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT * FROM users u WHERE u.role = p_role;
END; $$;


ALTER FUNCTION public.get_users_by_role(p_role character varying) OWNER TO postgres;

--
-- Name: insert_actor(character varying, character varying, integer, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_actor(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO actor (name, surname, year_of_birth, nationality)
    VALUES (p_name, p_surname, p_year_of_birth, p_nationality);
END;
$$;


ALTER PROCEDURE public.insert_actor(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying) OWNER TO postgres;

--
-- Name: insert_director(character varying, character varying, integer, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_director(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO director (name, surname, year_of_birth, nationality, biography)
    VALUES (p_name, p_surname, p_year_of_birth, p_nationality, p_biography);
END;
$$;


ALTER PROCEDURE public.insert_director(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying) OWNER TO postgres;

--
-- Name: insert_director(character varying, character varying, integer, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_director(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying, IN p_image_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO director (name, surname, year_of_birth, nationality, biography, image_path)
    VALUES (p_name, p_surname, p_year_of_birth, p_nationality, p_biography, p_image_path);
END;
$$;


ALTER PROCEDURE public.insert_director(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying, IN p_image_path character varying) OWNER TO postgres;

--
-- Name: insert_series(character varying, integer, integer, integer, integer, text, character varying, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series(IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_director_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO series (name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, poster_path, director_id)
    VALUES (p_name, p_seasons, p_episodes, p_year, p_end_year, p_desc, p_poster, p_director_id);
END;
$$;


ALTER PROCEDURE public.insert_series(IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_director_id integer) OWNER TO postgres;

--
-- Name: insert_series(character varying, integer, integer, integer, integer, text, character varying, character varying, character varying, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series(IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_genre character varying, IN p_platform character varying, IN p_director_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO series (
        name, number_of_seasons, number_of_episodes, 
        year_of_release, end_year, description, 
        poster_path, genre, platform, director_id
    )
    VALUES (
        p_name, p_seasons, p_episodes, 
        p_year, p_end_year, p_desc, 
        p_poster, p_genre, p_platform, p_director_id
    );
END;
$$;


ALTER PROCEDURE public.insert_series(IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_genre character varying, IN p_platform character varying, IN p_director_id integer) OWNER TO postgres;

--
-- Name: insert_series_actor(integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series_actor(IN p_sid integer, IN p_aid integer)
    LANGUAGE plpgsql
    AS $$ BEGIN INSERT INTO series_actor VALUES (p_sid, p_aid); END; $$;


ALTER PROCEDURE public.insert_series_actor(IN p_sid integer, IN p_aid integer) OWNER TO postgres;

--
-- Name: insert_series_actor(integer, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series_actor(IN p_series_id integer, IN p_actor_name character varying, IN p_actor_surname character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_actor_id INTEGER;
BEGIN
    -- 1. Pronađi glumca po imenu i prezimenu ili ga kreiraj ako ne postoji
    SELECT id INTO v_actor_id FROM public.actor 
    WHERE name = p_actor_name AND surname = p_actor_surname LIMIT 1;

    IF v_actor_id IS NULL THEN
        INSERT INTO public.actor (name, surname) 
        VALUES (p_actor_name, p_actor_surname) 
        RETURNING id INTO v_actor_id;
    END IF;

    -- 2. Poveži ga sa serijom (ON CONFLICT sprječava grešku ako su već povezani)
    INSERT INTO public.series_actor (series_id, actor_id)
    VALUES (p_series_id, v_actor_id)
    ON CONFLICT DO NOTHING;
END;
$$;


ALTER PROCEDURE public.insert_series_actor(IN p_series_id integer, IN p_actor_name character varying, IN p_actor_surname character varying) OWNER TO postgres;

--
-- Name: insert_series_actor(integer, character varying, character varying, integer, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series_actor(IN p_sid integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_actor_id integer;
BEGIN
    -- 1. Pokušaj naći glumca po imenu i prezimenu, ako ga nema - unesi ga
    -- Koristimo ON CONFLICT da izbjegnemo duplikate ako glumac već postoji
    INSERT INTO actor (name, surname, year_of_birth, nationality)
    VALUES (p_name, p_surname, p_year_of_birth, p_nationality)
    ON CONFLICT (name, surname) 
    DO UPDATE SET 
        year_of_birth = EXCLUDED.year_of_birth, 
        nationality = EXCLUDED.nationality
    RETURNING id INTO v_actor_id;

    -- 2. Poveži glumca sa serijom u tablici series_actor
    INSERT INTO series_actor (series_id, actor_id)
    VALUES (p_sid, v_actor_id)
    ON CONFLICT DO NOTHING; -- Ako su već povezani, ne radi ništa
END;
$$;


ALTER PROCEDURE public.insert_series_actor(IN p_sid integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying) OWNER TO postgres;

--
-- Name: insert_series_genre(integer, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series_genre(IN p_sid integer, IN p_genre character varying)
    LANGUAGE plpgsql
    AS $$ BEGIN INSERT INTO series_genre VALUES (p_sid, p_genre); END; $$;


ALTER PROCEDURE public.insert_series_genre(IN p_sid integer, IN p_genre character varying) OWNER TO postgres;

--
-- Name: insert_series_platform(integer, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_series_platform(IN p_sid integer, IN p_platform character varying)
    LANGUAGE plpgsql
    AS $$ BEGIN INSERT INTO series_platform VALUES (p_sid, p_platform); END; $$;


ALTER PROCEDURE public.insert_series_platform(IN p_sid integer, IN p_platform character varying) OWNER TO postgres;

--
-- Name: insert_user(character varying, character varying, character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_user(IN p_username character varying, IN p_password character varying, IN p_name character varying, IN p_surname character varying, IN p_email character varying, IN p_role character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO users (username, password, name, surname, email, role)
    VALUES (p_username, p_password, p_name, p_surname, p_email, p_role);
END;
$$;


ALTER PROCEDURE public.insert_user(IN p_username character varying, IN p_password character varying, IN p_name character varying, IN p_surname character varying, IN p_email character varying, IN p_role character varying) OWNER TO postgres;

--
-- Name: insert_watchlist_item(integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_watchlist_item(IN p_user_id integer, IN p_series_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO watchlist (user_id, series_id)
    VALUES (p_user_id, p_series_id)
    ON CONFLICT (user_id, series_id) DO NOTHING;
END;
$$;


ALTER PROCEDURE public.insert_watchlist_item(IN p_user_id integer, IN p_series_id integer) OWNER TO postgres;

--
-- Name: save_series_actor(integer, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.save_series_actor(IN p_series_id integer, IN p_actor_name character varying, IN p_actor_surname character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_actor_id INTEGER;
BEGIN
    SELECT id INTO v_actor_id FROM public.actor 
    WHERE name = p_actor_name AND surname = p_actor_surname LIMIT 1;

    IF v_actor_id IS NULL THEN
        INSERT INTO public.actor (name, surname) VALUES (p_actor_name, p_actor_surname) 
        RETURNING id INTO v_actor_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM public.series_actor WHERE series_id = p_series_id AND actor_id = v_actor_id) THEN
        INSERT INTO public.series_actor (series_id, actor_id) VALUES (p_series_id, v_actor_id);
    END IF;
END;
$$;


ALTER PROCEDURE public.save_series_actor(IN p_series_id integer, IN p_actor_name character varying, IN p_actor_surname character varying) OWNER TO postgres;

--
-- Name: actor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.actor (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    surname character varying(100) NOT NULL,
    year_of_birth integer,
    nationality character varying(100)
);


ALTER TABLE public.actor OWNER TO postgres;

--
-- Name: search_actors(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.search_actors(p_keyword text) RETURNS SETOF public.actor
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM actor a 
    WHERE LOWER(a.name) LIKE LOWER('%' || p_keyword || '%')
       OR LOWER(a.surname) LIKE LOWER('%' || p_keyword || '%');
END;
$$;


ALTER FUNCTION public.search_actors(p_keyword text) OWNER TO postgres;

--
-- Name: search_directors(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.search_directors(p_search text) RETURNS TABLE(id integer, name character varying, surname character varying, year_of_birth integer, nationality character varying, biography text, image_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT d.id, d.name, d.surname, d.year_of_birth, d.nationality, d.biography, d.image_path 
    FROM director d
    WHERE LOWER(d.name) LIKE LOWER('%' || p_search || '%') 
       OR LOWER(d.surname) LIKE LOWER('%' || p_search || '%');
END;
$$;


ALTER FUNCTION public.search_directors(p_search text) OWNER TO postgres;

--
-- Name: search_series(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.search_series(p_keyword text) RETURNS SETOF public.series
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM series s 
    WHERE LOWER(s.name) LIKE LOWER('%' || p_keyword || '%');
END;
$$;


ALTER FUNCTION public.search_series(p_keyword text) OWNER TO postgres;

--
-- Name: update_actor(integer, character varying, character varying, integer, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_actor(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE actor
    SET name          = p_name,
        surname       = p_surname,
        year_of_birth = p_year_of_birth,
        nationality   = p_nationality
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_actor(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying) OWNER TO postgres;

--
-- Name: update_actor(integer, character varying, character varying, integer, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_actor(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_image_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE actor
    SET name          = p_name,
        surname       = p_surname,
        year_of_birth = p_year_of_birth,
        nationality   = p_nationality,
        image_path    = p_image_path
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_actor(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_image_path character varying) OWNER TO postgres;

--
-- Name: update_director(integer, character varying, character varying, integer, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_director(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE director
    SET name          = p_name,
        surname       = p_surname,
        year_of_birth = p_year_of_birth,
        nationality   = p_nationality,
        biography     = p_biography
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_director(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying) OWNER TO postgres;

--
-- Name: update_director(integer, character varying, character varying, integer, character varying, text, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_director(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_birth integer, IN p_nat character varying, IN p_bio text, IN p_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE director 
    SET name = p_name, 
        surname = p_surname, 
        year_of_birth = p_birth, 
        nationality = p_nat, 
        biography = p_bio, 
        image_path = p_path
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_director(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_birth integer, IN p_nat character varying, IN p_bio text, IN p_path character varying) OWNER TO postgres;

--
-- Name: update_director(integer, character varying, character varying, integer, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_director(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying, IN p_image_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE director
    SET name          = p_name,
        surname       = p_surname,
        year_of_birth = p_year_of_birth,
        nationality   = p_nationality,
        biography     = p_biography,
        image_path    = p_image_path
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_director(IN p_id integer, IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying, IN p_biography character varying, IN p_image_path character varying) OWNER TO postgres;

--
-- Name: update_series(integer, character varying, integer, integer, integer, integer, text, character varying, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_series(IN p_id integer, IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_director_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE series 
    SET name = p_name, number_of_seasons = p_seasons, number_of_episodes = p_episodes, 
        year_of_release = p_year, end_year = p_end_year, description = p_desc, 
        poster_path = p_poster, director_id = p_director_id
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_series(IN p_id integer, IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_director_id integer) OWNER TO postgres;

--
-- Name: update_series(integer, character varying, integer, integer, integer, integer, text, character varying, character varying, character varying, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_series(IN p_id integer, IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_genre character varying, IN p_platform character varying, IN p_director_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE series 
    SET 
        name = p_name,
        number_of_seasons = p_seasons,
        number_of_episodes = p_episodes,
        year_of_release = p_year,
        end_year = p_end_year,
        description = p_desc,
        poster_path = p_poster,
        genre = p_genre,       -- Ažuriramo stupac
        platform = p_platform, -- Ažuriramo stupac
        director_id = p_director_id
    WHERE id = p_id;
END;
$$;


ALTER PROCEDURE public.update_series(IN p_id integer, IN p_name character varying, IN p_seasons integer, IN p_episodes integer, IN p_year integer, IN p_end_year integer, IN p_desc text, IN p_poster character varying, IN p_genre character varying, IN p_platform character varying, IN p_director_id integer) OWNER TO postgres;

--
-- Name: update_user(integer, character varying, character varying, character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_user(IN p_id integer, IN p_username character varying, IN p_password character varying, IN p_name character varying, IN p_surname character varying, IN p_email character varying, IN p_role character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE users 
    SET username = p_username, password = p_password, name = p_name, 
        surname = p_surname, email = p_email, role = p_role
    WHERE id = p_id;
END; $$;


ALTER PROCEDURE public.update_user(IN p_id integer, IN p_username character varying, IN p_password character varying, IN p_name character varying, IN p_surname character varying, IN p_email character varying, IN p_role character varying) OWNER TO postgres;

--
-- Name: actor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.actor_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.actor_id_seq OWNER TO postgres;

--
-- Name: actor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.actor_id_seq OWNED BY public.actor.id;


--
-- Name: director; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.director (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    surname character varying(100) NOT NULL,
    year_of_birth integer,
    nationality character varying(100),
    biography text
);


ALTER TABLE public.director OWNER TO postgres;

--
-- Name: director_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.director_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.director_id_seq OWNER TO postgres;

--
-- Name: director_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.director_id_seq OWNED BY public.director.id;


--
-- Name: series_actor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.series_actor (
    series_id integer NOT NULL,
    actor_id integer NOT NULL
);


ALTER TABLE public.series_actor OWNER TO postgres;

--
-- Name: series_genre; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.series_genre (
    series_id integer NOT NULL,
    genre character varying(50) NOT NULL
);


ALTER TABLE public.series_genre OWNER TO postgres;

--
-- Name: series_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.series_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.series_id_seq OWNER TO postgres;

--
-- Name: series_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.series_id_seq OWNED BY public.series.id;


--
-- Name: series_platform; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.series_platform (
    series_id integer NOT NULL,
    platform character varying(50) NOT NULL
);


ALTER TABLE public.series_platform OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    name character varying(100) NOT NULL,
    surname character varying(100) NOT NULL,
    email character varying(255) NOT NULL,
    role character varying(50) NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMINISTRATOR'::character varying, 'USER'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: watchlist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.watchlist (
    id integer NOT NULL,
    user_id integer NOT NULL,
    series_id integer NOT NULL,
    added_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.watchlist OWNER TO postgres;

--
-- Name: watchlist_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.watchlist_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.watchlist_id_seq OWNER TO postgres;

--
-- Name: watchlist_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.watchlist_id_seq OWNED BY public.watchlist.id;


--
-- Name: actor id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actor ALTER COLUMN id SET DEFAULT nextval('public.actor_id_seq'::regclass);


--
-- Name: director id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.director ALTER COLUMN id SET DEFAULT nextval('public.director_id_seq'::regclass);


--
-- Name: series id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series ALTER COLUMN id SET DEFAULT nextval('public.series_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: watchlist id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.watchlist ALTER COLUMN id SET DEFAULT nextval('public.watchlist_id_seq'::regclass);


--
-- Data for Name: actor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.actor (id, name, surname, year_of_birth, nationality) FROM stdin;
\.


--
-- Data for Name: director; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.director (id, name, surname, year_of_birth, nationality, biography) FROM stdin;
\.


--
-- Data for Name: series; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.series (id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, poster_path, director_id, genre, platform) FROM stdin;
\.


--
-- Data for Name: series_actor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.series_actor (series_id, actor_id) FROM stdin;
\.


--
-- Data for Name: series_genre; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.series_genre (series_id, genre) FROM stdin;
\.


--
-- Data for Name: series_platform; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.series_platform (series_id, platform) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, password, name, surname, email, role) FROM stdin;
1	admin	admin123	Admin	Sustava	admin@serije.hr	ADMINISTRATOR
3	macak	macak1	macak	macic	macak@mail.hr	USER
14	Proba	proba1	Probni	Korisnik	proba@mail.hr	USER
15	nikolasubic	nikolasubic	Nikola	Subic	nikola@mail.hr	USER
\.


--
-- Data for Name: watchlist; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.watchlist (id, user_id, series_id, added_at) FROM stdin;
\.


--
-- Name: actor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.actor_id_seq', 1, false);


--
-- Name: director_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.director_id_seq', 1, false);


--
-- Name: series_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.series_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 15, true);


--
-- Name: watchlist_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.watchlist_id_seq', 1, false);


--
-- Name: actor actor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actor
    ADD CONSTRAINT actor_pkey PRIMARY KEY (id);


--
-- Name: director director_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.director
    ADD CONSTRAINT director_pkey PRIMARY KEY (id);


--
-- Name: series_actor series_actor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_actor
    ADD CONSTRAINT series_actor_pkey PRIMARY KEY (series_id, actor_id);


--
-- Name: series_genre series_genre_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_genre
    ADD CONSTRAINT series_genre_pkey PRIMARY KEY (series_id, genre);


--
-- Name: series series_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series
    ADD CONSTRAINT series_pkey PRIMARY KEY (id);


--
-- Name: series_platform series_platform_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_platform
    ADD CONSTRAINT series_platform_pkey PRIMARY KEY (series_id, platform);


--
-- Name: actor unique_actor_name_surname; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actor
    ADD CONSTRAINT unique_actor_name_surname UNIQUE (name, surname);


--
-- Name: watchlist unique_user_series; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.watchlist
    ADD CONSTRAINT unique_user_series UNIQUE (user_id, series_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: watchlist watchlist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.watchlist
    ADD CONSTRAINT watchlist_pkey PRIMARY KEY (id);


--
-- Name: ux_director_name_surname; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX ux_director_name_surname ON public.director USING btree (lower((name)::text), lower((surname)::text));


--
-- Name: series_actor fk_sa_actor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_actor
    ADD CONSTRAINT fk_sa_actor FOREIGN KEY (actor_id) REFERENCES public.actor(id) ON DELETE CASCADE;


--
-- Name: series_actor fk_sa_series; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_actor
    ADD CONSTRAINT fk_sa_series FOREIGN KEY (series_id) REFERENCES public.series(id) ON DELETE CASCADE;


--
-- Name: watchlist fk_series; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.watchlist
    ADD CONSTRAINT fk_series FOREIGN KEY (series_id) REFERENCES public.series(id) ON DELETE CASCADE;


--
-- Name: series fk_series_director; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series
    ADD CONSTRAINT fk_series_director FOREIGN KEY (director_id) REFERENCES public.director(id) ON DELETE SET NULL;


--
-- Name: series_genre fk_sg_series; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_genre
    ADD CONSTRAINT fk_sg_series FOREIGN KEY (series_id) REFERENCES public.series(id) ON DELETE CASCADE;


--
-- Name: series_platform fk_sp_series; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.series_platform
    ADD CONSTRAINT fk_sp_series FOREIGN KEY (series_id) REFERENCES public.series(id) ON DELETE CASCADE;


--
-- Name: watchlist fk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.watchlist
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict 3hFEmCsOGItUdIrhGPQOHUus6ybRdGYNDVFjnTUt0y1F5XUa2598K6SFxabwpna

