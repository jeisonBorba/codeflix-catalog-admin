# Create docker networks
docker network create adm_videos_services

# Create directories and define permissions
mkdir -m 777 .docker
mkdir -m 777 .docker/keycloak

# Run docker containers
docker compose -f services/docker-compose.yml up -d
docker compose -f app/docker-compose.yml up -d

echo "Starting containers..."
sleep 20