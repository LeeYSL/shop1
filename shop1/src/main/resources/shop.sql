create table useraccount (
   userid varchar(10) primary key,
   password varchar(15),
   username varchar(20),
   phoneno varchar(20),
   postcode varchar(7),
   address varchar(30),
   email varchar(50),
   birthday datetime
);

drop table sale;

CREATE TABLE sale ( --주문 테이블(주문정보)
	saleid int PRIMARY KEY,--주문번호
	userid varchar(10) NOT NULL,--주문 고객 아이디
	saledate DATETIME ,--주문일자
	foreign KEY (userid) REFERENCES useraccount (userid)
);

drop table saleitem; --주문 상품 테이블
CREATE TABLE saleitem (

	saleid int ,
	seq int ,
	itemid int NOT NULL,
	quantity int,
	PRIMARY KEY (saleid, seq),
	foreign key (saleid) references sale (saleid),
	foreign key (itemid) references item (id)
);