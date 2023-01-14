create database `team_tactics`;
CREATE USER 'teamtactics' IDENTIFIED BY 'ttadmin';
GRANT ALL privileges ON `team_tactics`.* TO 'teamtactics'@'%';
