<?php

namespace App\Repository;

use App\Entity\Inscription;
use App\Entity\Cours;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class InscriptionRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Inscription::class);
    }

    // Vérifie si l'utilisateur est déjà inscrit à ce cours
    public function utilisateurDejaInscrit(int $utilisateurId, int $coursId): bool
    {
        return (bool) $this->createQueryBuilder('i')
            ->where('i.utilisateur = :utilisateur')
            ->andWhere('i.cours = :cours')
            ->setParameter('utilisateur', $utilisateurId)
            ->setParameter('cours', $coursId)
            ->getQuery()
            ->getOneOrNullResult();
    }

    // Vérifie si l'utilisateur est inscrit à un autre cours de la même catégorie dans les 5 derniers jours
    public function utilisateurInscritRecemmentDansCategorie(int $utilisateurId, int $categorieId): bool
    {
        return (bool) $this->createQueryBuilder('i')
            ->join('i.cours', 'c')
            ->where('i.utilisateur = :utilisateur')
            ->andWhere('c.categorie = :categorie')
            ->andWhere('i.dateInscription >= :dateLimite')
            ->setParameter('utilisateur', $utilisateurId)
            ->setParameter('categorie', $categorieId)
            ->setParameter('dateLimite', new \DateTime('-5 days'))
            ->getQuery()
            ->getOneOrNullResult();
    }
    public function countInscriptionsForCours(Cours $cours): int
    {
        return (int) $this->createQueryBuilder('i')
            ->select('COUNT(i.id)')
            ->where('i.cours = :cours')
            ->setParameter('cours', $cours)
            ->getQuery()
            ->getSingleScalarResult();
    }
}
