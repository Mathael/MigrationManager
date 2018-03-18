# Migration Manager
Une solution pour executer vos scripts de mises à jour au chargement de l'application.  
Warning: the project is currently not a RELEASE ! But it will be RELEASED soon !

## Description
Le projet a pour but d'apporter une solution concrête, automatisée et viable à la mise à jour de la base de données (ou execution de scripts) durant le chargement de l'application. 
Actuellement le projet est basé sur Spring mais il est possible de l'adapter un petit peu pour le faire correspondre à d'autres types de projets.

### Le problème
En mode developpeur, il est facile de développer son projet, casser la base de données et corriger cela.  

Mais en production ? Comment faire pour que l'intégralité de mes changements sur ma base de donnés soient appliqués sans oublier le moindre ALTER ou CREATE ou que sais-je ?  
- Et si en plus il s'agit d'un gros projet avec plusieurs développeurs ?  
- Et si c'est le stagiaire qui s'en charge ? Vous seriez confiant ?  
- Et si vous vous trompez de base de données ?  

Il parait évident qu'il faut mutualiser et automatiser les mise à jour de la base de données ainsi que les manipulation supplémentaires à faire à l'execution.  

### La solution
La machine ne faisant que ce qu'on lui demande (sanf erreur humaine), l'ensemble des mises à jour de la table s'execute alors via un service initialisé au démarrage de l'application.  
TODO suite à venir

## Installation
TODO

#### Spring config
>@Bean  
public MigrationManager migrationManager() {  
    return new MigrationManagerImpl();  
}  

#### Mandatory SQL
>DROP TABLE IF EXISTS `migration`;  
 CREATE TABLE `migration` (  
   `type` varchar(100) NOT NULL,  
   `last_migration_time` int(11) NOT NULL,  
   PRIMARY KEY (`type`)  
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8;  

#### Properties
>application.migration.script.package =   
 application.migration.database.file.location = migration  
 application.migration.database.file.extension = sql  

## Utilisation
TODO

#### Priority & Order
TODO