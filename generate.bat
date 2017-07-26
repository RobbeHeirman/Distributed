@echo off
cd lib
java -jar avro-tools-1.7.7.jar compile protocol ../Schemas/server.avpr ../src
java -jar avro-tools-1.7.7.jar compile protocol ../Schemas/light.avpr ../src
java -jar avro-tools-1.7.7.jar compile protocol ../Schemas/fridge.avpr ../src
java -jar avro-tools-1.7.7.jar compile protocol ../Schemas/user.avpr ../src
java -jar avro-tools-1.7.7.jar compile protocol ../Schemas/sensor.avpr ../src
cd ..