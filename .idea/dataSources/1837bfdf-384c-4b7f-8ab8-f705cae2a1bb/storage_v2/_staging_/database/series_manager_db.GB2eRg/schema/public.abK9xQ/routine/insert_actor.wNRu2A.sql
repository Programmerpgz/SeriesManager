create procedure insert_actor(IN p_name character varying, IN p_surname character varying, IN p_year_of_birth integer, IN p_nationality character varying)
    language plpgsql
as
$$
BEGIN
    INSERT INTO actor (name, surname, year_of_birth, nationality)
    VALUES (p_name, p_surname, p_year_of_birth, p_nationality);
END;
$$;

alter procedure insert_actor(varchar, varchar, integer, varchar) owner to postgres;

