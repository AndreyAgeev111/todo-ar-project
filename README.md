# TO DO LIST API 
Pet project with Tapir, Doobie, Http4s on Scala

## For local deploy

Build:
```bash
sbt docker:publishLocal
```
In repository's directory:
```bash
cd local
```
Run:
```bash
docker-compose up --build
```

## Deploy prepared app

Run:
```bash
docker-compose up 
```
## After deploy

| Swagger | http://localhost:8080/docs |
|---------|----------------------------|
