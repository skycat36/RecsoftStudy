delete from order_m;
delete from product;
delete from photo_u;
delete from usr;
delete from language;

delete from role;

-- Table language
INSERT INTO language (path_to_validation_errors, path_to_text_field, id, readable_name) VALUES ('src/main/resources/messages_property/message_errors/message_errors_en/', 'src/main/resources/messages_property/text_fields/text_fields_en/', 2, 'en');
INSERT INTO language (path_to_validation_errors, path_to_text_field, id, readable_name) VALUES ('src/main/resources/messages_property/message_errors/message_errors_ru/', 'src/main/resources/messages_property/text_fields/text_fields_ru/', 1, 'ru');

-- Table role
INSERT INTO role (id, name) VALUES (1, 'seller');
INSERT INTO role (id, name) VALUES (2, 'user');

-- Table usr
INSERT INTO usr (id, cash, email, fam, login, name, password, sec_name, role_id, language_id) VALUES (1, 0, '36furious@gmail.com', 'Попов', 'qwe', 'Евгений', '123', 'Дмитриевич', 1, 1);
INSERT INTO usr (id, cash, email, fam, login, name, password, sec_name, role_id, language_id) VALUES (176, 0, '36furious@gmail.com', 'l', 'l', 'l', 'l', 'l', 2, 1);
INSERT INTO usr (id, cash, email, fam, login, name, password, sec_name, role_id, language_id) VALUES (3, 1235, '36furious@gmail.com', 'Иванов', 'pff', 'Иван', 'p', 'Иванович', 1, 1);
INSERT INTO usr (id, cash, email, fam, login, name, password, sec_name, role_id, language_id) VALUES (178, 40000, '36furious@gmail.com', 'q', 'm', 'q', 'm', 'q', 2, 1);
INSERT INTO usr (id, cash, email, fam, login, name, password, sec_name, role_id, language_id) VALUES (2, 258402, '36furious@gmail.com', 'Попов', 'q', 'Евгений', 'q', 'Дмитриевич', 2, 1);

-- Table photo_u
INSERT INTO photo_u (id, name, id_user) VALUES (175, '3c7b458a-f26b-452d-9f49-57f3a0d4e90d.srqv9.jpg', 176);
INSERT INTO photo_u (id, name, id_user) VALUES (177, '420c7957-c3ab-499f-8642-cfa0604596b4.srqv9.jpg ', 178);


