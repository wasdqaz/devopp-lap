#!/bin/bash

set -e  # Dừng script nếu có lệnh lỗi

# ================================
# 📌 Biến cấu hình
# ================================
DOCKER_REPO="soulgalaxy"
REPO_URL="https://github.com/wasdqaz/devopp-lap"
MAIN_BRANCH="SoulGalaxy2312/Build-Push-Docker-Image"
SPECIAL_BRANCH="dev_vets_service"
SPECIAL_SERVICE="spring-petclinic-vets-service"
BUILD_DIR="petclinic_build"
MAX_RETRIES=3

# ================================
# 🧹 Xoá thư mục build cũ & tạo mới
# ================================
rm -rf "$BUILD_DIR"
mkdir "$BUILD_DIR"
cd "$BUILD_DIR"

# ================================
# 📥 Clone branch main với sparse checkout
# ================================
echo "📥 Cloning main branch..."
git clone --depth=1 --filter=blob:none --sparse --branch "$MAIN_BRANCH" "$REPO_URL" .
git sparse-checkout set $(cat <<EOF
spring-petclinic-admin-server
spring-petclinic-api-gateway
spring-petclinic-config-server
spring-petclinic-customers-service
spring-petclinic-discovery-server
spring-petclinic-genai-service
spring-petclinic-visits-service
EOF
)
MAIN_COMMIT=$(git rev-parse --short HEAD)
echo "✅ Main branch cloned at commit $MAIN_COMMIT"

# ================================
# 📥 Clone nhánh riêng cho SPECIAL_SERVICE
# ================================
# 📥 Clone nhánh riêng cho SPECIAL_SERVICE với sparse-checkout
echo "📥 Cloning $SPECIAL_SERVICE from branch $SPECIAL_BRANCH..."
git clone --depth=1 --filter=blob:none --sparse --branch "$SPECIAL_BRANCH" "$REPO_URL" "temp-$SPECIAL_SERVICE"

cd "temp-$SPECIAL_SERVICE"
git sparse-checkout set $SPECIAL_SERVICE
VETS_COMMIT=$(git rev-parse --short HEAD)
cd ..

mv "temp-$SPECIAL_SERVICE/$SPECIAL_SERVICE" ./
rm -rf "temp-$SPECIAL_SERVICE"
echo "✅ $SPECIAL_SERVICE ready at commit $VETS_COMMIT"


# ================================
# 🔧 Danh sách services
# ================================
SERVICES=(
  spring-petclinic-admin-server
  spring-petclinic-api-gateway
  spring-petclinic-config-server
  spring-petclinic-customers-service
  spring-petclinic-discovery-server
  spring-petclinic-genai-service
  spring-petclinic-vets-service
  spring-petclinic-visits-service
)

# ================================
# 🔨 Build & Push Docker images
# ================================
for SERVICE in "${SERVICES[@]}"; do
  echo "🔨 Building Docker image for $SERVICE..."
  ./mvnw -f "${SERVICE}/pom.xml" clean package -DskipTests

  # ⏩ Chọn tag phù hợp theo commit
  if [[ "$SERVICE" == "$SPECIAL_SERVICE" ]]; then
    TAG=$VETS_COMMIT
  else
    TAG=$MAIN_COMMIT
  fi

  docker build -t "$DOCKER_REPO/${SERVICE}:${TAG}" ./${SERVICE}

  echo "📤 Pushing Docker image for $SERVICE with tag $TAG..."
  attempt=1
  until docker push "$DOCKER_REPO/${SERVICE}:${TAG}"; do
    if (( attempt == MAX_RETRIES )); then
      echo "❌ Failed to push $SERVICE after $attempt attempts."
      exit 1
    fi
    echo "🔁 Retrying push ($attempt/$MAX_RETRIES)..."
    attempt=$((attempt + 1))
    sleep 5
  done
done

echo "✅ All Docker images built and pushed successfully!"
