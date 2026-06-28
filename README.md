# Product Management Platform

Aplicación fullstack para administración de productos, construida como portafolio técnico para demostrar experiencia en desarrollo backend con Java, APIs REST modernas, arquitectura hexagonal e integración con frontend React.

---

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Java 21 · Quarkus 3 · MongoDB · Redis · MapStruct · Lombok |
| Frontend | React 18 · TypeScript · Vite · Material UI |
| Infraestructura | Docker · Kubernetes · GitHub Actions |

---

## Estructura del repositorio

```text
product-management/
├── product-management-api/         Backend Java + Quarkus
│   ├── src/
│   ├── Dockerfile
│   └── build.gradle
├── product-management-web/         Frontend React + Vite
│   ├── src/
│   ├── Dockerfile
│   └── nginx.conf
├── k8s/                            Manifests de Kubernetes (stack completo)
│   ├── issuer.yaml
│   ├── secret.yaml
│   ├── configmap.yaml
│   ├── mongo.yaml
│   ├── redis.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── web-deployment.yaml
│   ├── web-service.yaml
│   └── ingress.yaml
├── docker/
│   └── gateway.conf                nginx gateway (Docker Compose)
├── postman/
│   ├── product-management.postman_collection.json
│   ├── product-management.local.postman_environment.json
│   └── product-management.k8s.postman_environment.json
├── .github/workflows/
│   ├── docker-publish.yml          CI/CD backend
│   └── docker-publish-web.yml      CI/CD frontend
├── docker-compose.yml
└── README.md
```

---

## Postman

Los archivos están en `postman/`.

| Archivo | Descripción |
|---|---|
| `product-management.postman_collection.json` | Colección principal (10 requests) |
| `product-management.local.postman_environment.json` | Environment local vía Docker Compose |
| `product-management.k8s.postman_environment.json` | Environment Kubernetes (`product.local`) |

**Pasos de importación:**

1. En Postman, ir a **Import** y seleccionar los tres archivos de la carpeta `postman/`.
2. En el selector de environments (esquina superior derecha), elegir **product-management — local** o **product-management — k8s** según corresponda.
3. Ejecutar los requests en orden: el request `01 - Create Product` captura el `productId` automáticamente para los siguientes requests.

> Para K8s, añadir `product.local` en `/etc/hosts` apuntando a la IP del Ingress controller antes de ejecutar la colección.

---

## Documentación detallada

Ver [`product-management-api/README.md`](product-management-api/README.md) para instrucciones completas de instalación, ejecución, endpoints y despliegue.
