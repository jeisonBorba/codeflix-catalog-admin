# Create docker networks
docker network create adm_videos_services
docker network create elastic

# Create directories and define permissions
sudo chown root app/filebeat/filebeat.docker.yml
mkdir -m 777 .docker
mkdir -m 777 .docker/es01
mkdir -m 777 .docker/keycloak
mkdir -m 777 .docker/filebeat

# Run docker containers
docker compose -f services/docker-compose.yml up -d
docker compose -f elk/docker-compose.yml up -d
#docker compose -f app/docker-compose.yml up -d

echo "Starting containers..."
sleep 20