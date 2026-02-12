# Projet – Sprint 0 (26-01-2026)

## 1. Objectif
Mettre en place l'environnement de développement et vérifier le déploiement initial des projets BackOffice (BO) et FrontOffice (FO) avec un contenu minimal ("HelloWorld").  
Préparer la base pour les sprints suivants.

---

## 2. Organisation des rôles

| Rôle      | Responsabilités |
|-----------|----------------|
| **TeamLead** | Vérifie les merges vers la branche `main` et supervise le déploiement. Supervise l'utilisation des frameworks BO/FO. |
| **Dev1** | Développe en utilisant le framework BO/FO. |
| **Dev2** | Développe le backend avec Spring Boot. |
| **Rotation** | Les rôles peuvent changer à chaque sprint pour répartir les tâches. |

---

## 3. Base de données
- Utiliser une **base relationnelle** au choix (MySQL, PostgreSQL, Oracle…).  
- Une seule base partagée par **BackOffice** et **FrontOffice**.  
- Créer les tables initiales nécessaires pour les tests.  

---

## 4. Projets

### 4.1 BackOffice (BO)
- Projet séparé.
- **Backend :** Spring Boot  
- **Frontend :** Spring MVC + framework JS (Vue ou React)
- Sécurisé selon le rôle (login, permissions)

### 4.2 FrontOffice (FO)
- Projet séparé.
- **Backend :** Spring Boot  
- **Frontend :** Spring MVC + framework JS
- Accessible publiquement (anonyme)

### 4.3 Branches
- **backend** → Contient le code backend (Spring Boot)  
- **frontend** → Contient le code frontend (MVC + JS)

---

## 5. Déploiement
- Déployer sur **Eloque** ou **Runaway**.  
- Contenu initial : `HelloWorld` uniquement.  
- Vérifier que les librairies Maven sont incluses.  
- Versions locales et production doivent être identiques.  

---

## 6. Framework
- Utiliser le framework existant pour BO et FO.  
- Ajouter toutes les dépendances nécessaires via Maven dans chaque branche.  

---

## 7. Sécurité
- **BO :** Accès restreint selon le rôle (admin, user, etc.)  
- **FO :** Accès public pour les tests  

---

## 8. Livrables Sprint 0
1. BO et FO déployés avec `HelloWorld`.  
2. Branches `backend` et `frontend` créées et fonctionnelles.  
3. Maven configuré et toutes les dépendances ajoutées.  
4. Test de connexion et d'accès :  
   - BO → accès restreint selon rôle  
   - FO → accès public  
5. URLs de test accessibles.
