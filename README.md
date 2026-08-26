# Base_API_SpringBoot

build code backend
ps -ef | grep java
nohup java -jar api-gp-nguyenvan.jar --server.port=8083 > api-gp-nguyenvan.log 2>&1 &

pm2 start "npm run start" --name fe_menu
