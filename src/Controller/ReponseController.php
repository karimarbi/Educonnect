<?php

namespace App\Controller;

use App\Entity\Reponse;
use App\Form\ReponseType;
use App\Repository\ReclamationRepository;
use App\Repository\ReponseRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/reponse')]
class ReponseController extends AbstractController
{
    #[Route('/', name: 'app_reponse_index', methods: ['GET'])]
    public function index(ReponseRepository $reponseRepository): Response
    {
        return $this->render('reponse/index.html.twig', [
            'reponses' => $reponseRepository->findAll(),
        ]);
    }

    #[Route('/new/{reclamationId}', name: 'app_reponse_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, ReclamationRepository $reclamationRepository, $reclamationId): Response
    {
        // Récupérer la réclamation à laquelle la réponse sera associée
        $reclamation = $reclamationRepository->find($reclamationId);

        if (!$reclamation) {
            throw $this->createNotFoundException('Réclamation non trouvée');
        }

        // Créer une nouvelle instance de Reponse
        $reponse = new Reponse();
        
        // Associer la réclamation à la réponse
        $reponse->setReclamation($reclamation);

        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            $entityManager->persist($reponse);
            $entityManager->flush();

            $this->addFlash('success', 'Votre réponse a été envoyée avec succès.');

            return $this->redirectToRoute('app_reclamation_show', ['id' => $reclamation->getId()]);
        }

        return $this->renderForm('reponse/new.html.twig', [
            'form' => $form,
            'reclamation' => $reclamation,
        ]);
    }

    #[Route('/{id}', name: 'app_reponse_show', methods: ['GET'])]
    public function show(Reponse $reponse): Response
    {
        return $this->render('reponse/show.html.twig', [
            'reponse' => $reponse,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_reponse_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Reponse $reponse, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ReponseType::class, $reponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            $this->addFlash('success', 'Votre réponse a été modifiée avec succès.');

            return $this->redirectToRoute('app_reclamation_show', ['id' => $reponse->getReclamation()->getId()]);
        }

        return $this->renderForm('reponse/edit.html.twig', [
            'reponse' => $reponse,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_reponse_delete', methods: ['POST'])]
    public function delete(Request $request, Reponse $reponse, EntityManagerInterface $entityManager): Response
    {
      
        if ($this->isCsrfTokenValid('delete'.$reponse->getId(), $request->request->get('_token'))) {
           
            $entityManager->remove($reponse);
            $entityManager->flush();
    
          
            $this->addFlash('success', 'Réponse supprimée avec succès.');
        }
    
        return $this->redirectToRoute('app_reclamation_show', ['id' => $reponse->getReclamation()->getId()]);
    }
    
}
