create table if not exists BusLine (
  line varchar(20) not null,
  description varchar(255),
  primary key (line)
);

create table if not exists BusStop (
  id varchar(20) not null,
  name varchar(255) not null,
  latlng geography(POINT,4326),
  primary key (id)
);

create table if not exists BusLineStop (
  stopId varchar(20) not null,
  lineId varchar(20) not null,
  sequenceNumber smallint not null,
  primary key(stopId, lineId,sequenceNumber),
  foreign key (stopId) references BusStop(id),
  foreign key (lineId) references BusLine(line)
);
