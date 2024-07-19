drop table if exists compilations_events, requests, events, locations, categories, users, compilations cascade;

create table if not exists users (
    id    bigint       generated by default as identity primary key,
    name  varchar(250) not null,
    email varchar(254) not null unique
    );

create table if not exists categories (
    id    bigint generated by default as identity primary key,
    name  varchar(50) not null unique
    );

create table if not exists locations (
    id    bigint generated by default as identity primary key,
    lat   double precision not null,
    lon   double precision not null,
    name  varchar(250)
    );

CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
declare
    dist float = 0;
    rad_lat1 float;
    rad_lat2 float;
    theta float;
    rad_theta float;
BEGIN
    IF lat1 = lat2 AND lon1 = lon2
    THEN
        RETURN dist;
    ELSE
        -- переводим градусы широты в радианы
        rad_lat1 = pi() * lat1 / 180;
        -- переводим градусы долготы в радианы
        rad_lat2 = pi() * lat2 / 180;
        -- находим разность долгот
        theta = lon1 - lon2;
        -- переводим градусы в радианы
        rad_theta = pi() * theta / 180;
        -- находим длину ортодромии
        dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

        IF dist > 1
            THEN dist = 1;
        END IF;

        dist = acos(dist);
        -- переводим радианы в градусы
        dist = dist * 180 / pi();
        -- переводим градусы в километры
        dist = dist * 60 * 1.8524;

        RETURN dist;
    END IF;
END;
'
LANGUAGE PLPGSQL;

create table if not exists events (
    id                      bigint generated by default as identity primary key,
    annotation              varchar(2000)                   not null,
    category_id             bigint                          not null references categories(id),
    created_on              timestamp without time zone     not null,
    description             varchar(7000)                   not null,
    event_date              timestamp without time zone     not null,
    initiator_id            bigint                          not null references users(id) on delete cascade,
    location_id             bigint                          not null references locations(id) on delete cascade,
    is_paid                 boolean                         not null,
    participant_limit       int                             not null,
    published_on            timestamp without time zone,
    is_request_moderation   boolean                         not null,
    state                   varchar(20)                     not null,
    title                   varchar(120)                    not null unique
    );

create table if not exists requests (
    id             bigint generated by default as identity primary key,
    created        timestamp without time zone   not null,
    event_id       bigint                        not null references events(id) on delete cascade,
    requester_id   bigint                        not null references users(id) on delete cascade,
    status         varchar(20)                   not null,
    constraint requester_unique unique (event_id, requester_id)
    );

create table if not exists compilations (
    id      bigint       generated by default as identity primary key,
    pinned  boolean      not null,
    title   varchar(50)  not null
    );

create table if not exists compilations_events (
    compilations_id   bigint   not null references compilations (id) on delete cascade,
    events_id         bigint   not null references events (id) on delete cascade,
    constraint compilations_events_unique unique (compilations_id, events_id)
    );