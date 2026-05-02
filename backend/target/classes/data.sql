-- Initial seed only: rows are inserted when missing. This file is NOT updated when you change prices in the Admin UI.
-- The running database (menu_items table) is the source of truth after first startup.
-- Admin UI → PUT /api/menu-items/{id} saves to the DB only; nothing writes back to this .sql file.
-- To change defaults for new installs, edit the INSERT values below in source control.

INSERT INTO shop_settings (id, merchant_vpa, merchant_name, currency)
SELECT 1, 'yourshop@upi', 'Ramanujam & Janagam Family Cafe', 'INR'
WHERE NOT EXISTS (SELECT 1 FROM shop_settings WHERE id = 1);

INSERT INTO menu_items (name, description, price, image_url, active, sort_order, created_at, updated_at)
SELECT 'Idly', 'Steamed rice cakes (2 pcs)', 1.50, '/menu/idlly.jpg', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Idly');

INSERT INTO menu_items (name, description, price, image_url, active, sort_order, created_at, updated_at)
SELECT 'Parotta', 'Steamed rice flour and coconut', 3.00, '/menu/parotta.jpg', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Parotta');

INSERT INTO menu_items (name, description, price, image_url, active, sort_order, created_at, updated_at)
SELECT 'Tea', 'Hot tea', 3.00, '/menu/tea.png', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Tea');

INSERT INTO menu_items (name, description, price, image_url, active, sort_order, created_at, updated_at)
SELECT 'Coffee', 'Filter coffee', 4.00, '/menu/coffe.jpg', 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Coffee');

INSERT INTO menu_items (name, description, price, image_url, active, sort_order, created_at, updated_at)
SELECT 'Dosai', 'Crispy rice and lentil crepe', 7.00, '/menu/dosa.jpg', 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Dosai');

INSERT INTO menu_items (name, description, price, image_url, active, sort_order, created_at, updated_at)
SELECT 'Vada', 'Savory lentil doughnut (2 pcs)', 5.00, '/menu/vadai.jpg', 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE name = 'Vada');
