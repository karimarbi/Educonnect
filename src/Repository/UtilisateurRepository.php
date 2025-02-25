<?php

namespace App\Repository;

use App\Entity\Utilisateur;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Utilisateur>
 *
 * @method array findStatsByLieu()
 * @method array findAgeStats()
 * @method array findUsersGroupedByAgeRange()
 */
class UtilisateurRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Utilisateur::class);
    }

    public function findStatsByLieu(): array
    {
        return $this->createQueryBuilder('u')
            ->select('u.lieu, COUNT(u.id) as total')
            ->groupBy('u.lieu')
            ->getQuery()
            ->getResult();
    }

    public function findAgeStats(): array
    {
        return $this->createQueryBuilder('u')
            ->select('AVG(u.age) as moyenne, MIN(u.age) as minimum, MAX(u.age) as maximum')
            ->getQuery()
            ->getSingleResult();
    }

    public function findUsersGroupedByAgeRange(): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT 
                    CASE
                        WHEN age < 20 THEN '<20'
                        WHEN age BETWEEN 20 AND 29 THEN '20-29'
                        WHEN age BETWEEN 30 AND 39 THEN '30-39'
                        ELSE '40+'
                    END as ageRange,
                    COUNT(*) as total
                FROM utilisateur
                GROUP BY ageRange
                ORDER BY ageRange";
        // Exécute directement la requête et récupère un Statement compatible avec DBAL 3
        $stmt = $conn->executeQuery($sql);
        return $stmt->fetchAllAssociative();
    }
}
