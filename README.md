# LibraS API - Documentation

## Introduction

Ce projet est une API Spring Boot pour une application mobile de bibliothèque virtuelle de bandes dessinées, intégrée avec une base de données PostgreSQL. Cette documentation vous guidera à travers les étapes nécessaires pour configurer et exécuter l'application en utilisant Docker.

## Prérequis

- Docker et Docker Compose installés sur votre machine (Docker Desktop recommandé)
- Maven installé (si vous souhaitez construire le projet localement sans Docker)
- JDK 22 installé (si vous souhaitez construire le projet localement sans Docker)

## Structure du Projet

Le projet est divisé en deux principaux composants :

1. **API Spring Boot** : Fournit les endpoints REST pour l'application mobile.
2. **Base de Données PostgreSQL** : Stocke les données de l'application.

## Instructions d'Installation

### Étape 1 : Cloner le Dépôt

Clonez le dépôt du projet depuis GitHub

### Étape 2 : Configurer Firebase

Pour utiliser Firebase pour le stockage des images, vous devez configurer un compte Firebase et obtenir un fichier JSON de service compte. Suivez les étapes de la documentation officielle de Firebase pour créer un projet Firebase et télécharger le fichier JSON de service compte.

Placez ce fichier JSON dans le répertoire `src/main/resources/config`.

### Étape 3 : Configurer les Propriétés de l'Application

Vous devez d'abord générer les clés API nécessaires pour SerpAPI et Firebase.

- **SerpAPI** : Inscrivez-vous sur [SerpAPI](https://serpapi.com/) et générez une clé API.
- **Firebase** : Suivez les étapes mentionnées à l'Étape 2 pour obtenir le fichier JSON de service compte de Firebase.

Ensuite, ajoutez les lignes suivantes à votre fichier `application.properties` :

```properties
serpapi.key=VOTRE_CLE_API_SERPAPI
firebase.service-account.path=classpath:config/firebase-service-account.json
```

Ces configurations sont nécessaires pour utiliser l'API SerpAPI pour la recherche d'images inversée et Firebase pour le stockage des images.

### Étape 4 : Lancer les Services
Utilisez Docker Compose pour construire et lancer l'application :

```sh
docker-compose up --build
```

### Étape 5 : Utilisation de Maven (Optionnel)
Si vous souhaitez construire le projet localement sans Docker, utilisez Maven :

```sh
mvn clean package -DskipTests
```

Puis, exécutez l'application avec la commande suivante :

```sh
java -jar target/app.jar
```

### Configuration de la Base de Données

La base de données PostgreSQL est configurée pour être initialisée avec un fichier SQL (`init.sql`). Assurez-vous que ce fichier est présent dans le répertoire racine du projet et contient les commandes SQL nécessaires pour initialiser votre base de données.

### Endpoints

#### Authentification

- **POST /api/auth/signup** : Inscription d'un nouvel utilisateur
- **POST /api/auth/login** : Authentification d'un utilisateur

#### Livres

- **GET /api/books** : Récupère la liste de tous les livres disponibles
- **GET /api/books/discover** : Récupère les informations pour la page de découverte
- **GET /api/book-details/{bookId}** : Récupère les détails d'un livre spécifique
- **POST /api/books/set-base-desc** : Initialise les descriptions de base des livres
- **POST /api/books/by-tags** : Récupère les livres par tags
- **GET /api/books/recent** : Récupère les livres récemment ajoutés
- **POST /api/book/{bookId}/switch-in-user-library** : Ajoute/retire un livre de la bibliothèque utilisateur
- **POST /api/book/update** : Met à jour les informations d'un livre
- **GET /api/book/by-user** : Récupère les livres de la bibliothèque utilisateur
- **GET /api/books/search/{search}** : Recherche de livres par terme

#### Scan

- **POST /api/scan** : Téléchargement d'une image pour scanner la couverture d'une BD

#### Tags

- **GET /api/tags** : Récupère tous les tags disponibles
