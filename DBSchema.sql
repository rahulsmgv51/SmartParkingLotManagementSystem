-- public.parking_spot definition

-- Drop table

-- DROP TABLE public.parking_spot;

CREATE TABLE public.parking_spot (
id bigserial NOT NULL,
floor_no int4 NULL,
spot_no varchar(255) NULL,
vehicle_type varchar(255) NULL,
occupied bool NULL,
CONSTRAINT parking_spot_pkey PRIMARY KEY (id),
CONSTRAINT uk_spot_no UNIQUE (spot_no)
);


-- public.vehicle definition

-- Drop table

-- DROP TABLE public.vehicle;

CREATE TABLE public.vehicle (
id bigserial NOT NULL,
vehicle_number varchar(255) NULL,
vehicle_type varchar(255) NULL,
first_seen timestamp(6) NULL,
last_seen timestamp(6) NULL,
CONSTRAINT uk_vehicle_number UNIQUE (vehicle_number),
CONSTRAINT vehicle_pkey PRIMARY KEY (id)
);


-- public.ticket definition

-- Drop table

-- DROP TABLE public.ticket;

CREATE TABLE public.ticket (
ticket_id varchar(255) NOT NULL,
vehicle_number varchar(255) NULL,
vehicle_type varchar(255) NULL,
spot_id int8 NULL,
entry_time timestamp NULL,
exit_time timestamp NULL,
amount float8 NULL,
status varchar(255) NULL,
CONSTRAINT ticket_pkey PRIMARY KEY (ticket_id)
);
CREATE UNIQUE INDEX uk_active_vehicle ON public.ticket USING btree (vehicle_number) WHERE ((status)::text = 'ACTIVE'::text);


-- public.ticket foreign keys

ALTER TABLE public.ticket ADD CONSTRAINT fk_ticket_spot FOREIGN KEY (spot_id) REFERENCES public.parking_spot(id);