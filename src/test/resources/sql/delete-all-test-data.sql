delete from `custom_utilization_content` where 1;
delete from `custom_utilization_field` where 1;
delete from `reservation` where 1;
delete from `store_chair` where 1;
delete from `store_table` where 1;
delete from `store_space` where 1;
delete from `store` where 1;
delete from `user` where 1;

alter table `custom_utilization_field` AUTO_INCREMENT=1;
alter table `store_chair` AUTO_INCREMENT=1;
alter table `store_table` AUTO_INCREMENT=1;
alter table `store_space` AUTO_INCREMENT=1;
alter table `store` AUTO_INCREMENT=1;
alter table `user` AUTO_INCREMENT=1;