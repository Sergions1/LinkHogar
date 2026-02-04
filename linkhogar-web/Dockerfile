# Usamos una imagen ligera de Node 20
FROM node:20-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Instalamos Angular CLI globalmente
RUN npm install -g @angular/cli

# Copiamos los archivos de dependencias primero (para aprovechar la caché de Docker)
COPY package*.json ./

# Instalamos las dependencias del proyecto
RUN npm install

# Copiamos el resto del código
COPY . .

# Exponemos el puerto de Angular
EXPOSE 4200

# COMANDO DE ARRANQUE:
# --host 0.0.0.0: Para que sea accesible desde fuera del contenedor
# --poll 2000: CRÍTICO en Windows/Docker. Obliga a revisar cambios en los archivos cada 2s.
# Sin --poll, el "Hot Reload" no funciona bien en Docker.
CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "2000"]
