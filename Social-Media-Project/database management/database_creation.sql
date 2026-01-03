START TRANSACTION;

-------------------- DROP TABLES --------------------
DROP TABLE IF EXISTS comment_likes;
DROP TABLE IF EXISTS post_likes;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS member_company;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS tag;

-------------------- REMOVE EXTENSIONS --------------------
DROP EXTENSION IF EXISTS pg_trgm;


-------------------- ADD EXTENSIONS --------------------
CREATE EXTENSION IF NOT EXISTS pg_trgm; -- Enables Fuzzy Keyword Matching


-------------------- MAKE TABLES --------------------
CREATE TABLE member (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    salt VARCHAR (255) NOT NULL,
    stored_hash VARCHAR (100) NOT NULL
);

CREATE TABLE company (
    id SERIAL PRIMARY KEY,
	name VARCHAR (255) NOT NULL,
	description VARCHAR (255)
);


CREATE TABLE member_company (
    member_id INT,
	company_id INT,
	CONSTRAINT fk_member_company_member_id FOREIGN KEY (member_id) REFERENCES member (id),
	CONSTRAINT fk_member_company_company_id FOREIGN KEY (company_id) REFERENCES company (id)
);

CREATE TABLE post (
    id SERIAL PRIMARY KEY,
	title varchar(255),
	content TEXT,
    member_id INT,
	num_of_likes BIGINT DEFAULT 0,
	created_on_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE comment (
    id SERIAL PRIMARY KEY,
    content TEXT,
    parent_post_id INT,
    parent_comment_id INT,
	root_post_id INT NOT NULL,
    member_id INT,
	num_of_likes BIGINT DEFAULT 0,
	created_on_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_comment_member_id FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_comment_post_id FOREIGN KEY (parent_post_id) REFERENCES post (id),
    CONSTRAINT fk_comment_parent_comment_id FOREIGN KEY (parent_comment_id) REFERENCES comment (id),
    CONSTRAINT chk_parent CHECK (
        (parent_post_id IS NOT NULL AND parent_comment_id IS NULL) OR 
        (parent_post_id IS NULL AND parent_comment_id IS NOT NULL)
    ),
	CONSTRAINT chk_comment_not_its_own_parent CHECK (parent_comment_id != id),
	CONSTRAINT fk_root_post_id FOREIGN KEY (root_post_id) REFERENCES post (id)
);

CREATE TABLE post_likes (
	post_id INT NOT NULL,
    member_id INT NOT NULL,
    CONSTRAINT fk_post_likes_member_id FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_post_likes_post_id FOREIGN KEY (post_id) REFERENCES post (id),
    CONSTRAINT unique_post_likes UNIQUE (post_id, member_id)
);

CREATE TABLE comment_likes (
	comment_id INT NOT NULL,
	member_id INT NOT NULL,
	CONSTRAINT fk_comment_likes_member_id FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_comment_likes_comment_id FOREIGN KEY (comment_id) REFERENCES comment (id),
    CONSTRAINT unique_comment_like UNIQUE (comment_id, member_id)
);

CREATE TABLE tag (
	name VARCHAR(50) PRIMARY KEY UNIQUE
);

CREATE TABLE post_tag (
	post_id INT NOT NULL,
	tag_name VARCHAR(50) NOT NULL,
	CONSTRAINT fk_post_tag_post_id FOREIGN KEY (post_id) REFERENCES post (id),
	CONSTRAINT fk_post_tag_tag_id FOREIGN KEY (tag_name) REFERENCES tag (name)
);


-------------------- Create Tags --------------------
INSERT INTO tag (name)
	VALUES ('All');
	
INSERT INTO tag (name)
	VALUES ('Personal');

INSERT INTO tag (name)
	VALUES ('News');
 


COMMIT;


----------------------------------------------------------------
----------------------------------------------------------------





