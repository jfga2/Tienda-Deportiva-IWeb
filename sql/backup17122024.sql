--
-- PostgreSQL database dump
--

-- Dumped from database version 13.16 (Debian 13.16-1.pgdg120+1)
-- Dumped by pg_dump version 13.16 (Debian 13.16-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY public.usuarios DROP CONSTRAINT usuarios_pkey;
ALTER TABLE public.usuarios ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE public.usuarios_id_seq;
DROP TABLE public.usuarios;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: iweb
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    administrador boolean NOT NULL,
    bloqueado boolean NOT NULL,
    email character varying(255) NOT NULL,
    fecha_nacimiento date,
    nombre character varying(255),
    password character varying(255)
);


ALTER TABLE public.usuarios OWNER TO iweb;

--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: iweb
--

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuarios_id_seq OWNER TO iweb;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: iweb
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: iweb
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: iweb
--

COPY public.usuarios (id, administrador, bloqueado, email, fecha_nacimiento, nombre, password) FROM stdin;
2	t	f	admin@gmail.com	1999-01-01	admin	admin
4	f	f	user@gmail.com	1997-01-01	user	user
6	f	f	user3@gmail.com	1999-01-01	user3	user3
7	f	f	user4@gmail.com	1900-01-01	user4	user4
8	f	f	user5@gmail.com	1990-01-01	user5	user5
9	f	f	user6@gmail.com	2004-02-01	user6	user6
10	f	f	user7@gmail.com	2005-02-01	user7	user7
5	f	f	user2@gmail.com	1999-01-01	user2	user2
\.


--
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: iweb
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 10, true);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: iweb
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

