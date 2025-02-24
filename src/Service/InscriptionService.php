<?php

namespace App\Service;

use App\Entity\Inscription;
use App\Repository\InscriptionRepository;
use Doctrine\ORM\EntityManagerInterface;

class InscriptionService
{
    private InscriptionRepository $inscriptionRepository;
    private EntityManagerInterface $entityManager;

    public function __construct(InscriptionRepository $inscriptionRepository, EntityManagerInterface $entityManager)
    {
        $this->inscriptionRepository = $inscriptionRepository;
        $this->entityManager = $entityManager;
    }

    public function inscrireUtilisateur($utilisateur, $cours): string
    {
        if ($this->inscriptionRepository->utilisateurDejaInscrit($utilisateur->getId(), $cours->getId())) {
            return "Vous êtes déjà inscrit à ce cours.";
        }

        if ($this->inscriptionRepository->utilisateurInscritRecemmentDansCategorie($utilisateur->getId(), $cours->getCategorie()->getId())) {
            return "Vous devez attendre 5 jours avant de vous inscrire à un autre cours de cette catégorie.";
        }

        $inscription = new Inscription();
        $inscription->setUtilisateur($utilisateur);
        $inscription->setCours($cours);
        $inscription->setDateInscription(new \DateTime()); // Enregistre la date actuelle

        $this->entityManager->persist($inscription);
        $this->entityManager->flush();

        return "Inscription réussie !";
    }
}
