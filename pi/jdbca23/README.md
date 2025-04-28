# Configuration du projet dans IntelliJ IDEA

Ce document contient les instructions pour configurer correctement le projet dans IntelliJ IDEA.

## Prérequis

Avant de commencer, assurez-vous d'avoir installé les logiciels suivants :

1. **Java Development Kit (JDK) 17** : [Télécharger JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
2. **IntelliJ IDEA** : [Télécharger IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
3. **Scene Builder** : [Télécharger Scene Builder](https://gluonhq.com/products/scene-builder/)
4. **Maven** : [Télécharger Maven](https://maven.apache.org/download.cgi)
5. **MySQL** : [Télécharger MySQL](https://dev.mysql.com/downloads/mysql/)

## Configuration automatique

Pour configurer automatiquement le projet, exécutez les scripts suivants dans l'ordre :

1. `setup_intellij.bat` : Vérifie que tous les prérequis sont installés et ouvre le projet dans IntelliJ IDEA.
2. `setup_database.bat` : Configure la base de données MySQL.
3. `run_project.bat` : Compile et exécute le projet.

## Configuration manuelle

Si vous préférez configurer le projet manuellement, suivez les instructions ci-dessous.

### Configuration du SDK

1. Ouvrez le projet dans IntelliJ IDEA
2. Allez dans `File > Project Structure` (ou appuyez sur `Ctrl+Alt+Shift+S`)
3. Dans la section `Project`, sélectionnez le SDK Java 17 (ou la version que vous avez installée)
4. Cliquez sur `Apply` puis `OK`

### Configuration de Maven

1. Assurez-vous que Maven est correctement configuré dans IntelliJ IDEA
2. Allez dans `File > Settings > Build, Execution, Deployment > Build Tools > Maven`
3. Vérifiez que le chemin vers Maven est correct
4. Cliquez sur `Apply` puis `OK`

### Configuration de Scene Builder

1. Allez dans `File > Settings > Languages & Frameworks > JavaFX`
2. Dans le champ "Path to SceneBuilder", entrez le chemin vers l'exécutable de Scene Builder
3. Cliquez sur `Apply` puis `OK`

### Configuration de la base de données

1. Assurez-vous que MySQL est installé et en cours d'exécution sur votre machine
2. Exécutez le script `setup_database.sql` dans MySQL pour créer la base de données et la table
3. Si vous avez modifié les paramètres de connexion dans `DatabaseConnection.java`, assurez-vous qu'ils correspondent à votre configuration MySQL

## Exécution du projet

### Dans IntelliJ IDEA

1. Ouvrez la classe `esprit.tn.main.MainFX`
2. Cliquez sur le bouton vert de lecture à côté de la méthode `main`
3. Sélectionnez `Run 'MainFX.main()'`

### Avec Maven

1. Ouvrez un terminal dans le dossier du projet
2. Exécutez la commande `mvn javafx:run`

## Résolution des problèmes courants

### Erreur de connexion à la base de données

Si vous rencontrez une erreur de connexion à la base de données, vérifiez les points suivants :
- MySQL est en cours d'exécution
- La base de données `esprit` existe
- Les paramètres de connexion dans `DatabaseConnection.java` sont corrects

### Erreur de compilation

Si vous rencontrez une erreur de compilation, vérifiez les points suivants :
- Le SDK Java est correctement configuré
- Maven est correctement configuré
- Toutes les dépendances sont téléchargées (vous pouvez exécuter `mvn clean install` dans le terminal)

### Erreur d'exécution de JavaFX

Si vous rencontrez une erreur lors de l'exécution de JavaFX, vérifiez les points suivants :
- Les dépendances JavaFX sont correctement configurées dans le fichier `pom.xml`
- Le plugin JavaFX Maven est correctement configuré 