<?php
namespace App\Controller;

use App\Entity\Cours;
use App\Entity\Utilisateur;
use App\Entity\Inscription;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Doctrine\ORM\EntityManagerInterface;

class InscriptionController extends AbstractController
{
    private EntityManagerInterface $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }

    #[Route('/inscription/{userId}/{coursId}', name: 'inscription_cours')]
    public function inscrire(
        int $userId, 
        int $coursId
    ): Response {
        $user = $this->entityManager->getRepository(Utilisateur::class)->find($userId);
        $cours = $this->entityManager->getRepository(Cours::class)->find($coursId);

        if (!$user || !$cours) {
            $this->addFlash('error', 'Utilisateur ou cours introuvable.');
            return $this->redirectToRoute('app_cours_indexf');
        }

        // Vérifier si l'utilisateur est déjà inscrit
        foreach ($user->getInscriptions() as $inscription) {
            if ($inscription->getCours() === $cours) {
                $this->addFlash('warning', 'Vous êtes déjà inscrit à ce cours.');
                return $this->redirectToRoute('app_cours_indexf');
            }
        }

        // Vérifier si l'utilisateur a un cours dans la même catégorie dans les 5 derniers jours
        foreach ($user->getInscriptions() as $inscription) {
            if ($inscription->getCours()->getCategorie() === $cours->getCategorie()) {
                $dateInscription = $inscription->getDateInscription();
                if ($dateInscription && $dateInscription->diff(new \DateTime())->days < 5) {
                    $this->addFlash('warning', 'Vous devez attendre 5 jours avant de vous inscrire à un cours de la même catégorie.');
                    return $this->redirectToRoute('app_cours_indexf');
                }
            }
        }

        // Enregistrer l'inscription
        $inscription = new Inscription();
        $inscription->setUtilisateur($user);
        $inscription->setCours($cours);
        $inscription->setDateInscription(new \DateTime());

        $this->entityManager->persist($inscription);
        $this->entityManager->flush();

        $this->addFlash('success', 'Inscription réussie au cours : ' . $cours->getTitre());

        return $this->redirectToRoute('app_cours_indexf');
    }
}
