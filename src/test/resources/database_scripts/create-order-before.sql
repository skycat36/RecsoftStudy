delete from size_usr;
delete from status;
delete from user_prod_com;
delete from prod_size;
delete from category;
delete from order_m;
delete from product;


-- Table size_usr
INSERT INTO size_usr (id, name_size) VALUES (1, 'S');
INSERT INTO size_usr (id, name_size) VALUES (2, 'M');
INSERT INTO size_usr (id, name_size) VALUES (3, 'L');
INSERT INTO size_usr (id, name_size) VALUES (4, 'XL');
INSERT INTO size_usr (id, name_size) VALUES (5, 'XXl');

-- Table status
INSERT INTO status (id, name) VALUES (1, 'not_done');
INSERT INTO status (id, name) VALUES (3, 'done');
INSERT INTO status (id, name) VALUES (2, 'in_process');

-- Table user_prod_com
INSERT INTO user_prod_com (user_id, prod_id, comment, id) VALUES (178, 3, 'jklkjjjjjjjjjjjjjjjjjj', 179);
INSERT INTO user_prod_com (user_id, prod_id, comment, id) VALUES (178, 3, 'qwerty', 183);
INSERT INTO user_prod_com (user_id, prod_id, comment, id) VALUES (3, 3, 'ku', 263);
INSERT INTO user_prod_com (user_id, prod_id, comment, id) VALUES (3, 283, 'good', 285);
INSERT INTO user_prod_com (user_id, prod_id, comment, id) VALUES (2, 8, 'qwrert', 289);

-- Table prod_size
INSERT INTO prod_size (id_prod, id_size) VALUES (1, 1);
INSERT INTO prod_size (id_prod, id_size) VALUES (1, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (2, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (2, 5);
INSERT INTO prod_size (id_prod, id_size) VALUES (4, 2);
INSERT INTO prod_size (id_prod, id_size) VALUES (5, 1);
INSERT INTO prod_size (id_prod, id_size) VALUES (5, 2);
INSERT INTO prod_size (id_prod, id_size) VALUES (5, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (6, 4);
INSERT INTO prod_size (id_prod, id_size) VALUES (7, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (8, 1);
INSERT INTO prod_size (id_prod, id_size) VALUES (8, 2);
INSERT INTO prod_size (id_prod, id_size) VALUES (196, 4);
INSERT INTO prod_size (id_prod, id_size) VALUES (196, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (3, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (3, 2);
INSERT INTO prod_size (id_prod, id_size) VALUES (283, 1);
INSERT INTO prod_size (id_prod, id_size) VALUES (28, 2);
INSERT INTO prod_size (id_prod, id_size) VALUES (10, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (1, 2);
INSERT INTO prod_size (id_prod, id_size) VALUES (4, 4);
INSERT INTO prod_size (id_prod, id_size) VALUES (6, 3);
INSERT INTO prod_size (id_prod, id_size) VALUES (7, 2);

-- Table category
INSERT INTO category (id, name) VALUES (1, 'Штаны');
INSERT INTO category (id, name) VALUES (2, 'Майки');
INSERT INTO category (id, name) VALUES (3, 'Куртки');
INSERT INTO category (id, name) VALUES (4, 'Перчатки');
INSERT INTO category (id, name) VALUES (5, 'Шлема');
INSERT INTO category (id, name) VALUES (6, 'Черепахи');
INSERT INTO category (id, name) VALUES (7, 'Аксессуары');

-- Table order_m
INSERT INTO order_m (id, adress, status_id, user_id, product_id, count_p, pay) VALUES (290, 'zambiya', 1, 2, 8, 2, true);
INSERT INTO order_m (id, adress, status_id, user_id, product_id, count_p, pay) VALUES (121, 'zambiya', 1, 3, 3, 2, false);
INSERT INTO order_m (id, adress, status_id, user_id, product_id, count_p, pay) VALUES (123, 'zambiya', 1, 3, 7, 1, false);
INSERT INTO order_m (id, adress, status_id, user_id, product_id, count_p, pay) VALUES (122, 'zambiya', 1, 3, 3, 2, false);
INSERT INTO order_m (id, adress, status_id, user_id, product_id, count_p, pay) VALUES (119, 'zambiya', 1, 3, 10, 2, false);
INSERT INTO order_m (id, adress, status_id, user_id, product_id, count_p, pay) VALUES (120, 'zambiya', 1, 3, 10, 1, false);

-- Table product
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (28, 1, 'mlk', 10, 0, 0, 'qwertyzghf', 321, 7);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (196, 3, 'lk,l;k', 0, null, null, 'fsdfgs', 2321, 1);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (7, 7, 'Код товара: FLY-PANT-EVOLUTION-SPIKE-WHITE-RED', 0, 0, 0, 'FLY Racing - Штаны Evolution 2.0 Spike бело-красные', 9919, 1);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (5, 8, 'Код товара: 10050-016', 0, 0, 0, 'FOX Racing - Защита панцирь Fox Titan Sport Jacket Black/Orange', 11290, 6);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (3, 2, 'Код товара: FLY-JERSEY-EVOLUTION-SPIKE-RED-WHITE', 0, 0, 0, 'FLY Racing - Майка Fly Evolution Spike красно-белая', 2239, 2);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (283, 10, 'Линейка кроссовых очков GMZ3 среди продукции HZ Goggles позиционируется как топовая в своей категории. Великолепный дизайн, удобная посадка очков и отличный обзор удовлетворят требования самых строгих райдеров.', 0, null, null, 'HZ Goggles - Очки Blazing Red', 4000, 7);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (8, 3, 'Код товара: 2822-0511', 41, 0, 0, 'ICON - Куртка ICON Anthem Mesh женская розовая', 11789, 3);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (2, 3, 'LS2 - Шлем FF352 Atmos черно-красный', 0, 0, 0, 'LS2 - Шлем FF352 Atmos черно-красный', 5980, 5);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (6, 4, 'Код товара: 2901-4657', 0, 0, 0, 'Thor - Штаны S5 Phase Tilt черные', 5889, 1);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (1, 2, 'Just1 - топовые Итальянские шлемы для Off-road дисциплин. Шлемы Just1 не просто новый бренд на рынке, а продукт, созданный с использованием самых современных технологий и инноваций. ', 30, 0, 0, 'Just1 - Шлем J12 Mister X Blue', 19950, 5);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (4, 9, 'Код товара: 12351-018', 0, 0, 0, 'FOX Racing - Защита панцирь Fox Raptor Vest ', 14590, 6);
INSERT INTO product (id, count, description, discount, dislike_p, like_p, name, price, category_id) VALUES (10, 4, 'qwerty', 10, 0, 0, 'qwerty', 4321, 5);
