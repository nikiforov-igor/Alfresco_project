CREATE TABLE IF NOT EXISTS "RECORDSCOUNT"
(
  "ID" bigint NOT NULL,
  "COUNT" bigint,
  CONSTRAINT "RECORDSCOUNT_PK" PRIMARY KEY ("ID")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "RECORDSCOUNT"
  OWNER TO postgres;


CREATE TABLE IF NOT EXISTS "SEQUENCE_TABLE"
(
  "SEQUENCE_NAME" character varying(255) NOT NULL,
  "NEXT_VAL" bigint NOT NULL,
  CONSTRAINT "SEQUENCE_TABLE_PK" PRIMARY KEY ("SEQUENCE_NAME")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "SEQUENCE_TABLE"
  OWNER TO postgres;

CREATE TABLE IF NOT EXISTS "BUSINESSJOURNALSTORERECORD"
(
  "DATE" timestamp with time zone NOT NULL,
  "DUMMY" integer NOT NULL,
  "NODEID" bigint NOT NULL,
  "EVENTCATEGORY" character varying(255),
  "EVENTCATEGORYTEXT" character varying(255),
  "INITIATOR" character varying(255),
  "INITIATORTEXT" character varying(255),
  "MAINOBJECT" character varying(255),
  "MAINOBJECTDESCRIPTION" character varying(255),
  "OBJECT1" character varying(10485760),
  "OBJECT1ID" character varying(255),
  "OBJECT2" character varying(255),
  "OBJECT2ID" character varying(255),
  "OBJECT3" character varying(255),
  "OBJECT3ID" character varying(255),
  "OBJECT4" character varying(255),
  "OBJECT4ID" character varying(255),
  "OBJECT5" character varying(255),
  "OBJECT5ID" character varying(255),
  "OBJECTTYPE" character varying(255),
  "OBJECTTYPETEXT" character varying(255),
  "RECORDDESCRIPTION" character varying(10485760),
  CONSTRAINT "BUSINESSJOURNALSTORERECORD_PK" PRIMARY KEY ("DUMMY", "DATE", "NODEID")
)
WITH (
  OIDS=FALSE
);

ALTER TABLE "BUSINESSJOURNALSTORERECORD"
  OWNER TO postgres;
