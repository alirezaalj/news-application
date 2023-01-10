MERGE INTO USERS( ID,EMAIL,NAME, PASSWORD,CREATE_AT , UPDATE_AT ,ROLE)
values (1, 'admin@gmail.com','admin','$2a$10$vJWfUw/MP2dDBuqOyQ6fKONxJeVwiQ3xCRQAg/eEyWfm8S6qsz36i','2021-09-09 00:00:00','2021-09-09 00:00:00','ROLE_ADMIN'),
       (2, 'user@gmail.com','user','$2a$10$vJWfUw/MP2dDBuqOyQ6fKONxJeVwiQ3xCRQAg/eEyWfm8S6qsz36i','2021-09-09 00:00:00','2021-09-09 00:00:00','ROLE_USER');