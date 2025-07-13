# Используем JDK 17 на базе Ubuntu Jammy
FROM eclipse-temurin:17-jdk-jammy

# Рабочая директория внутри контейнера
WORKDIR /app

# Аргумент с именем скомпилированного jar-файла
ARG JAR_FILE=target/*.jar

# Копируем jar в контейнер и переименовываем в app.jar
COPY ${JAR_FILE} app.jar

# По-умолчанию запускаем приложение командой java -jar
ENTRYPOINT ["java", "-jar", "app.jar"]
