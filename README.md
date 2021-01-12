# INF5153 : Generation Procédurale de Terrain

  - Auteurs : Sébastien Mosser, Frédéric Alas
  - Version : 2020.12
  
## Idée générale du projet

Le but du projet est de définir un générateur procédural de terrain, qui va recevoir en entrée un maillage géométrique (un ensemble de polygones pavant le plan). Sur la base de ce maillage, le programme va fabriquer un terrain comportant divers attributs, et produire en sortie un maillage "enrichi" qui pourra être visualisé par l'utilisateur. 

Étant donné le visualiseur et le générateur de maillage déjà fourni, l'objectif du projet était de concevoir et développer un générateur de terrain.

## Description du dépôt 

Le dépot de code est un projet _Maven_ classique. Le repertoire `src/main/java` contient le code source de l'application, et le répertoire `src/test/java` les tests unitaires.

Le projet vient avec deux outils externes pour gérer la dimension "mathématique" des terrains générés : _(i)_ un générateur de maillages (`tools/generator-X.y.jar`) et _(ii)_ un visualisateur de maillages (`tools/visualizer-X.y.jar`). A la date de remise du projet, ces deux outils sont toujours en version `1.0` (donc X=1 et y=0).

Des maillages de références sont fournis, dans le repertoire `sample`. Chaque maillage de référence est identifié selon la convention `sample-W-H-P.mesh`, où `W` est la largeur du maillage, `H` sa hauteur et `P` son degré de précision (en nombre de polygones).

Pour compiler le projet, il suffit d'invoquer Maven, à partir de la racine du projet, depuis la ligne de commande : 

```
projet-ptg % mvn clean package
``` 

Une fois le projet compilé, la version actuelle prend en charge les options suivantes:
_(i)_ `-i` pour spécifier le maillage fourni en entrée.
_(ii)_ `-o` pour spécifier le nom du maillage enrichi qui sera fourni en sorti.
_(iii)_ `-shape` pour specifier la forme de l'île générée (atoll ou tortuga).
_(iv)_ `-altitude` (optionnel) pour spécifier une valeur numérique d'altitude maximale.
_(v)_ `-seed` (optionnel) pour spécifier une valeur de référence afin d'éliminer le degré aléatoire de la génération.
_(vi)_ `-water` et `-soil` pour spécifier le nombre de points d'eau (surface ou non) et le niveau d'humidité du sol.
_(vii)_ `-archipelago` (optionnel) pour spécifier le nombre d'îles à générer sur le terrain.
_(viii)_ `-heatmap` (optionnel) pour spécifier le type de coloration du terrain, prend les _humidity_, _altitude_ 


```
projet-ptg % mvn exec:java -Dexec.args="-i samples/sample-1000-1000-4096.mesh -o demo.mesh -shape tortuga -altitude 3 -water 3 -soil regular"
``` 

Ou, plus simplement, vous pouvez utiliser le script `ptg.sh` fourni, qui fait le relais avec Maven : 

```
projet-ptg % mvn exec:java -Dexec.args="-i samples/sample-1000-1000-4096.mesh -o demo.mesh -shape tortuga -altitude 3 -water 3 -soil regular"
```

## Outils disponibles

Les deux outils nécéssitent une version de Java égale ou supérieure à 13 pour s'éxecuter. 

## Rapport

Il est possible de trouver un fichier `rapport.md` à la racine du projet, ce fichier comprend des justifications et réflexions liées aux choix de conception du projet.

## Diagrammes

Un répertoire _diagrammes_ contient une multitude de diagrammes aidant la compréhension du projet. Notamment un diagramme d'objets, de séquence et de classes représentant 
le projet à différents points du développement. 

### Visualisation de maillages (`tools/visualizer-X.y.jar`)

Pour visualiser un maillage, vous avez l'outil à disposition l'outil `visualizer` dans le repertoire `tools`. C'est un jar auto-exécutable, qui répond aux options suivantes : 

  - `-i` : le maillage a visualiser (défaut : `data.mesh`)
  - `-o` : le fichier où la visualisation sera enregistrée (défaut : `output.svg`)
  - `-t` : le format de la visualisation (défaut : `svg`)

L'outil de visualisation peut générer des images vectorielles (formats `svg` ou `pdf`), ou _bitmap_ (formats `png` ou `jpg`). 

```
projet-ptg/tools % java -jar visualizer-1.0.jar -o demo.png -t png -i samples/sample-1000-800-512.mesh 
projet-ptg/tools % java -jar visualizer-1.0.jar -o demo.pdf -t pdf -i samples/sample-1000-800-512.mesh 
```

La visualisation dispose d'un mode de déboguage (`-d`), qui va afficher sur la sortie standard une description textuelle du maillage, et générer une image qui dessine uniquement les polygones et leurs relations de voisinages.

```
projet-ptg/tools % java -jar visualizer-1.0.jar -d -o demo.png -t png -i samples/sample-1000-800-512.mesh > description.txt
```

### Génération de maillages (`tools/generator-X.y.jar`)

Si vous souhaitez génerer vos propre maillages, vous pouvez utiliser l'outil `generator` dans le répertoire `tools`. C'est un jar auto-exécutable, qui répond aux options suivantes : 

  - `-o` : fichier de sortie pour enregistrer le maillage (défaut : `data.mesh`)
  - `-W` : largeur (_width_) du maillage (défaut : `1000`)
  - `-H` : hauteur (_height_) du maillage (défaut : `1000`)
  - `-P` : précision (nombre de polygones) du maillage (défaut : `512`)
  - `-L` : coefficient de relaxation (arrondissement) de Lloyd (défaut : `10`)

Pour génerer dans le fichier `demo.mesh` un maillage de 750 pixels de large et 500 pixels de haut, incluant 300 polygones, il faut donc invoquer : 

```
projet-ptg/toolstools % java -jar generator-1.0.jar -o demo.mesh -W 750 -H 500 -P 300
```

## Dépendances logicielle & Écosystème disponible

### Test unitaires : JUnit 4.12

Une suite de tests _JUnit_ se lance automatiquement lors de la compilation du projet. 

### Gestion de la ligne de commande : Apache Commons CLI 1.4

Cette bibliothèque permet de gérer facilement l'analyse de la ligne de commande pour extraire les options de configuration du générateur de terrain. Vous pouvez vous inspirer de l'exemple fourni

### Intégration continue : Github Actions

Dans le repertoire `.github/workflows` se trouve un _pipeline_ pour compiler votre projet à chaque `push` sur Github (fichier `java.yaml`)
