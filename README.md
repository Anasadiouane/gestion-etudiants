# Gestion du Transport Scolaire — Association El Kheir

## À propos du projet

Ce projet est une **initiative personnelle à vocation sociale et solidaire**, réalisée bénévolement dans le but d’aider l’association **El Kheir**, une association locale de notre région, à améliorer la gestion de son service de transport scolaire.

L’objectif est de mettre à la disposition de l’association un outil informatique simple, pratique et accessible, lui permettant de mieux organiser le suivi des élèves transportés depuis différents douars, la gestion des parents (tuteurs légaux) ainsi que le suivi des paiements mensuels.

Cette solution a été conçue pour répondre aux besoins concrets de l’association, en facilitant les tâches administratives quotidiennes et en réduisant la dépendance aux documents papier et aux fichiers Excel dispersés.

Le projet s’inscrit dans une démarche de **partage des compétences informatiques au service de la communauté locale**, en utilisant le développement logiciel pour apporter une contribution concrète à une initiative associative.

## Présentation de l’application

**Gestion du Transport Scolaire** est une application de bureau destinée à fonctionner localement sous Windows, sans connexion Internet ni configuration manuelle d’un serveur.

Elle propose une interface bilingue en **français et en arabe**, avec prise en charge de l’affichage de droite à gauche (RTL) pour l’arabe.

## Fonctionnalités

- **Gestion des élèves :** ajout, modification, archivage, liste paginée, filtres par sexe et par douar, recherche par nom d’élève ou de parent.
- **Gestion des parents :** association de plusieurs enfants à un même parent et création rapide d’un parent depuis le formulaire d’un élève.
- **Gestion des paiements :**
  - Saisie des paiements d’inscription et des mensualités pour un ou plusieurs enfants.
  - Génération automatique des numéros de bons de paiement (`B-00001`, `B-00002`, etc.).
  - Prise en charge des paiements partiels.
  - Tableau de suivi des paiements par élève et par mois, de septembre à juin.
  - Suivi des montants dus, payés, restants et des éventuels crédits des parents.
  - Filtrage des élèves dont les paiements ne sont pas entièrement réglés.
- **Exportation et impression :** export des listes d’élèves, des tableaux de paiements et des soldes vers Excel, avec possibilité d’impression et de génération de PDF.
- **Paramètres :** configuration des tarifs, gestion de la nouvelle année scolaire, changement du mot de passe et sauvegarde de la base de données.
- **Utilisation simplifiée :** lancement depuis le bureau Windows, prévention des instances multiples, icône dans la zone de notification et sauvegarde automatique quotidienne.

## Règles de gestion

| Élément | Règle |
|---|---|
| Frais d’inscription | 30 DH, une seule fois par parent et par année scolaire, quel que soit le nombre d’enfants |
| Transport mensuel | 100 DH pour le premier enfant et 50 DH pour chaque enfant supplémentaire |
| Tarif personnalisé | Possibilité de définir un tarif spécifique pour un élève |
| Année scolaire | De septembre à juin, soit 10 mois |
| Départ d’un élève | La facturation s’arrête à partir du mois de sortie, tout en conservant l’historique des paiements |
| Arrivée en cours d’année | La facturation commence à partir du mois d’entrée |
| Douars pris en charge | Lamnizla, Talbort, Talaïnt, Tamda et Ighzer Aghanime |

Les tarifs peuvent être modifiés depuis la rubrique **Paramètres**.

## Technologies utilisées

| Couche | Technologies |
|---|---|
| Interface utilisateur | Angular 19 — composants standalone |
| Backend et API REST | Spring Boot 3.3, Spring Security, Spring Data JPA |
| Base de données | SQLite |
| Langage backend | Java 21 |
| Packaging Windows | `jpackage` et Inno Setup 6 |

## Architecture technique

```text
Interface Angular compilée
          │
          ▼
Spring Boot — 127.0.0.1:8081
          │
          ├── API REST (/api/**)
          │
          ▼
Base de données SQLite
          │
          └── data.db
```

En production, l’application fonctionne dans un seul processus : Spring Boot sert à la fois l’API REST et l’interface Angular compilée.

Le serveur écoute uniquement sur l’adresse locale `127.0.0.1`, ce qui évite d’exposer l’application directement aux autres appareils du réseau.

## Structure du dépôt

```text
gestion-etudiants-backend/
├── src/main/java/             Code Java
├── src/main/resources/static/ Interface Angular compilée
├── build-exe.bat              Génération de l’exécutable
├── build-installer.bat        Génération de l’installateur
└── installer.iss              Configuration Inno Setup

frontend/                      Sources Angular
README.md
```

## Lancement en développement

### Prérequis

- JDK 21
- Maven ou le wrapper Maven fourni avec le projet

1. Ouvrir le dossier `gestion-etudiants-backend` dans IntelliJ IDEA.
2. Lancer la classe principale `GestionEtudiantsBackendApplication`.
3. Accéder à l’application depuis [http://localhost:8081](http://localhost:8081).

Pour désactiver le lancement automatique du navigateur et de l’icône de notification pendant le développement, utiliser l’argument :

```text
--app.launcher.enabled=false
```

### Modification de l’interface Angular

Prérequis : Node.js 20 ou 22.

```bash
cd frontend
npm install
npm start
```

L’interface de développement sera accessible à l’adresse [http://localhost:4200](http://localhost:4200), avec un proxy vers le backend sur le port 8081.

Pour compiler l’interface et l’intégrer au backend :

```bash
npm run deploy
```

### Génération de l’exécutable et de l’installateur Windows

Prérequis :

- JDK 21 avec `jpackage` disponible dans le `PATH`.
- [Inno Setup 6](https://jrsoftware.org/isdl.php).

Depuis le dossier `gestion-etudiants-backend` :

```bat
build-exe.bat
build-installer.bat
```

L’installateur généré est :

```text
dist/Setup-GestionEtudiants-1.0.0.exe
```

Il constitue le fichier à remettre à l’utilisateur final. L’installation est conçue pour fonctionner sans demander de droits administrateur.

Pour modifier la version, mettre à jour `AppVersion` dans `installer.iss` et `--app-version` dans `build-exe.bat`.

## Stockage des données et sauvegardes

Les données sont enregistrées localement dans un fichier SQLite, en dehors du répertoire d’installation :

```text
%LOCALAPPDATA%\GestionEtudiants\
├── data.db
├── backups/    Sauvegardes automatiques quotidiennes
└── logs/       Journaux de l’application
```

- Les sauvegardes automatiques conservent les 30 derniers jours.
- La désinstallation du logiciel ne supprime pas les données.
- Une sauvegarde manuelle peut être téléchargée depuis la rubrique **Paramètres**.
- Pour restaurer une sauvegarde, fermer l’application, remplacer `data.db` par la copie souhaitée, puis relancer le logiciel.

## Sécurité et confidentialité

- Un compte administrateur est créé au premier lancement. Il est recommandé de modifier immédiatement le mot de passe initial.
- Les données des élèves et des parents, notamment les numéros de téléphone et les informations de paiement, doivent rester confidentielles.
- Le fichier `data.db` et ses sauvegardes ne doivent jamais être publiés sur un dépôt public.
- Le fichier `.gitignore` est configuré pour exclure les données locales sensibles du dépôt.

## Impact social

Au-delà de l’aspect technique, ce projet représente une contribution personnelle à une initiative associative locale. Il vise à faciliter la gestion du transport scolaire, à améliorer le suivi administratif et à permettre aux membres de l’association de consacrer moins de temps aux tâches manuelles.

Il illustre également une volonté de mettre les compétences acquises en développement logiciel au service d’un besoin réel, avec une solution adaptée aux moyens et aux contraintes d’une association locale.

## Auteur

**Anas Adiouane**  
Ingénieur d’État en Génie Informatique

Projet personnel à vocation sociale, réalisé bénévolement pour l’association **El Kheir**.

Dépôt privé.
