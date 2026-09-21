insert into inventory_items (product_id, available_quantity) values ('P1', 100) on conflict (product_id) do nothing;
insert into inventory_items (product_id, available_quantity) values ('P2', 5) on conflict (product_id) do nothing;
insert into inventory_items (product_id, available_quantity) values ('P3', 0) on conflict (product_id) do nothing;
