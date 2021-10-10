--liquibase formatted sql
--changeset oleg@briolink.com:1
--splitStatements false
use ${writeSchema}


INSERT INTO industry(id,name) VALUES ('14b60a98-1e84-4657-af4e-b9b46a0fe3bc','Social');
INSERT INTO industry(id,name) VALUES ('6763e338-457a-408c-9935-63e26101227c','Internet');
INSERT INTO industry(id,name) VALUES ('4e5d6b07-2779-48b2-a6d8-b0249d058e9d','Software');
INSERT INTO industry(id,name) VALUES ('84303076-6477-4c78-b24a-91f9cdc1b8a7','Trade');
INSERT INTO industry(id,name) VALUES ('b70e4e34-9b68-43af-8026-0f277b6b0d1b','Deisgn');
INSERT INTO industry(id,name) VALUES ('7ded339f-999c-4c42-9db0-b80fccfa33f1','Beauty');
INSERT INTO industry(id,name) VALUES ('04881229-ebc7-46a1-a42e-237794c87cb0','Music');
INSERT INTO industry(id,name) VALUES ('9b9a8ab3-4899-4904-9e83-118e6fae1522','Health');
INSERT INTO industry(id,name) VALUES ('e6bff832-dd2a-4451-9e58-c7c008a7b0ba','Transport');
INSERT INTO industry(id,name) VALUES ('5e352386-e80f-4489-8dc4-b24975e741b2','Hardware');
INSERT INTO industry(id,name) VALUES ('2b3faf20-6236-4ff5-98dc-269a3c214bfc','Consulting');

INSERT INTO occupation(id,name) VALUES ('cab8111d-3bf7-4c56-871d-37aa1e1a6361','Consulting');
INSERT INTO occupation(id,name) VALUES ('dbadd3f3-24ba-4897-b5ba-34e14c95e7fe','Technology');
INSERT INTO occupation(id,name) VALUES ('9ac6ffa8-b109-4936-912c-956d0347fd7b','Computer Systems Analyst');
INSERT INTO occupation(id,name) VALUES ('9a65eed5-3933-4604-9796-54458a2c4906','Software');
INSERT INTO occupation(id,name) VALUES ('e2e3cbed-70ff-4f5e-8347-a61c2e52ae49','Hardware');

INSERT INTO keyword(id,name) VALUES ('1763c0ec-8704-4b32-bcc4-590e6d27a3c0','Device');
INSERT INTO keyword(id,name) VALUES ('d1c66ba1-9b04-4179-a816-15b129a7940a','Electronic');
INSERT INTO keyword(id,name) VALUES ('f2a3b949-a9ad-4bbb-a81b-43fba5e269b2','Innovative');
INSERT INTO keyword(id,name) VALUES ('5b844f0c-de9a-4c14-91fb-978f9f865288','Telephone');
INSERT INTO keyword(id,name) VALUES ('fcb3cef6-2983-40de-b67a-a6e9c4805dd3','Retail');
INSERT INTO keyword(id,name) VALUES ('d2308497-7369-4689-a248-b082ee6063ca','Inthernet');
INSERT INTO keyword(id,name) VALUES ('d555af6e-fd00-4872-8e2d-4547fa5d5f13','Search');
INSERT INTO keyword(id,name) VALUES ('3832bfed-fa70-466d-b4b5-32c86b1ac05b','Technology');
INSERT INTO keyword(id,name) VALUES ('c658ba40-ac1d-47a9-b6dc-736ea840c8a0','Trade');
INSERT INTO keyword(id,name) VALUES ('849e58f0-fb6c-42ae-b1da-5484256a89b5','Design');

INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('e9f89daa-dd37-4595-a2ac-4bc4f6aaac88','paypal','Pay Pal','https://www.paypal.com','https://www.paypalobjects.com/webstatic/icon/pp258.png','USA','California','San-Mateo','5e352386-e80f-4489-8dc4-b24975e741b2','9a65eed5-3933-4604-9796-54458a2c4906','paypal','paypal',0,'PayPal is the safer, easier way to pay and get paid online. The service allows anyone to pay in any way they prefer, including through credit cards, bank accounts, PayPal Smart Connect or account balances, without sharing financial information. PayPal has quickly become a global leader in online payment solutions with more than  million accounts worldwide. Available in 202 countries and 25 currencies around the world, PayPal enables global ecommerce by making payments possible across different locations, currencies, and languages.  PayPal has received more than 20 awards for excellence from the internet industry and the business community - most recently the 2006 Webby Award for Best Financial Services Site and the 2006 Webby People''s Voice Award for Best Financial Services Site.  Located in San Jose, California, PayPal was founded in 1998. PayPal (Europe) S.à r.l. et Cie, S.C.A. is a credit institution (or bank) authorised and supervised by Luxembourg’s financial regulator, the Commission de Surveillance du Secteur Financier (or CSSF). CSSF’s registered office: 283, route d’Arlon, L-1150 Luxembourg.');
INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('728e5333-6f00-4de1-bf0f-1ace3374c47b','amazon','Amazon','https://www.amazon.com/','https://regnum.ru/uploads/pictures/news/2018/11/30/regnum_picture_154356037138044_normal.png','USA','Texas','Houston','04881229-ebc7-46a1-a42e-237794c87cb0','9a65eed5-3933-4604-9796-54458a2c4906','amazon','amazon',1,'When Amazon.com launched in 1995, it was with the mission “to be Earth’s most customer-centric company.” What does this mean? It''s simple. We''re a company that obsesses over customers. Our actions, goals, projects, programmes and inventions begin and end with the customer at the forefront of our minds. In other words, we start with the customer and work backwards. When we hit on something that is really working for customers, we commit to it in the hope that it will turn into an even bigger success. However, it’s not always as straightforward as that. Inventing is messy, and over time, it’s certain that we’ll fail at some big bets too.');
INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('974ae794-a929-434d-a939-b0fb49d8bf64','notion-labs-inc','Notion Labs INC','https://www.notion.so/','https://upload.wikimedia.org/wikipedia/commons/4/45/Notion_app_logo.png','USA','California','Los Angeles','5e352386-e80f-4489-8dc4-b24975e741b2','dbadd3f3-24ba-4897-b5ba-34e14c95e7fe','notion-labs-inc','notion-labs-inc',1,'You probably have fifteen tabs open: one for email, one for Slack, one for Google Docs, and on, and on…But have you ever thought description where these "work tools" came from? Or why there are so many of them?To answer these questions, and to explain why we created Notion, we have to travel back in time.');
INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('95d3b063-8ab5-4abb-9887-4006db7a20a8','google','Google','https://www.google.com/','https://w7.pngwing.com/pngs/760/624/png-transparent-google-logo-google-search-advertising-google-company-text-trademark.png','USA','Texas','Dallas','14b60a98-1e84-4657-af4e-b9b46a0fe3bc','9ac6ffa8-b109-4936-912c-956d0347fd7b','google','google',1,'American multinational technology company that specializes in Internet-related services and products, which include online advertising technologies, a search engine, cloud computing, software, and hardware. It is considered one of the Big Five companies in the American information technology industry, along with Amazon, Facebook, Apple, and Microsoft. Google was founded on September 4, 1998, by Larry Page and Sergey Brin while they were Ph.D. students at Stanford University in California. Together they own description 14% of its publicly-listed shares and control 56% of the stockholder voting power through super-voting stock. The company went public via an initial public offering (IPO) in 2004. In 2015, Google was reorganized as a wholly-owned subsidiary of Alphabet Inc.. Google is Alphabet''s largest subsidiary and is a holding company for Alphabet''s Internet properties and interests. Sundar Pichai was appointed CEO of Google on October 24, 2015, replacing Larry Page, who became the CEO of Alphabet. On December 3, 2019, Pichai also became the CEO of Alphabet.');
INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('1e325d92-38b2-420a-a79e-4504110e0959','twitter','Twitter','https://twitter.com/','https://upload.wikimedia.org/wikipedia/ru/thumb/9/9f/Twitter_bird_logo_2012.svg/1261px-Twitter_bird_logo_2012.svg.png','Russian','Utah','Moscow','4e5d6b07-2779-48b2-a6d8-b0249d058e9d','e2e3cbed-70ff-4f5e-8347-a61c2e52ae49','twitter','twitter',1,'Twitter is an American microblogging and social networking service on which users post and interact with messages known as "tweets". Registered users can post, like, and retweet tweets, but unregistered users can only read those that are publicly available. Users interact with Twitter through browser or mobile frontend software, or programmatically via its APIs. Prior to April 2020 services were accessible via SMS. The service is provided by Twitter, Inc., a corporation based in San Francisco, California, and has more than 25 offices around the world.[14] Tweets were originally restricted to 140 characters, but the limit was doubled to 280 for non-CJK languages in November 2017. Audio and video tweets remain limited to 140 seconds for most accounts.');
INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('765ca9e5-1bc0-4c94-abad-63e75ab3aa7c','github','Git Hub','https://github.com/','https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png','USA','California','San Francisco','9b9a8ab3-4899-4904-9e83-118e6fae1522','9a65eed5-3933-4604-9796-54458a2c4906','github','github',1,'GitHub, Inc. is a provider of Internet hosting for software development and version control using Git. It offers the distributed version control and source code management (SCM) functionality of Git, plus its own features. It provides access control and several collaboration features such as bug tracking, feature requests, task management, continuous integration and wikis for every project. Headquartered in California, it has been a subsidiary of Microsoft since 2018.');
INSERT INTO company(id,slug,name,website,logo,country,state,city,industry_id,occupation_id,facebook,twitter,is_type_public,description) VALUES ('b7151133-a500-42e7-812c-419a0f7f0349','silicon-valley-innovation-center','Silicon Valley Innovation Center','https://siliconvalley.center/','https://storage.theoryandpractice.ru/tnp/uploads/image_logo/000/036/065/image/medium_1603e7bfd3.png','USA','California','San-Mateo','b70e4e34-9b68-43af-8026-0f277b6b0d1b','9a65eed5-3933-4604-9796-54458a2c4906','silicon-valley-innovation-center','silicon-valley-innovation-center',1,'We help traditional companies take the full advantage of the new technological revolution to transform themselves into  technology-powered businesses. Our clients include hundreds of top executives and board directors of Fortune 2000 companies, who are successfully transforming their companies. It’s time now for you to join this revolution');


