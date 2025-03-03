<?php

namespace App\Controller\Admin;

use App\Entity\Utilisateur;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Form\UtilisateurType;


#[Route('/admin')]
class CrudUsersController extends AbstractController
{
    #[Route('/utilisateurs', name: 'list_utilisateurs', methods: ['GET'])]
    public function list(EntityManagerInterface $entityManager): Response
    {
        $utilisateurs = $entityManager->getRepository(Utilisateur::class)->findAll();

        return $this->render('users_list/users_list.html.twig', [
            'utilisateurs' => $utilisateurs,
        ]);
    }

    #[Route('/utilisateurs/supprimer/{id}', name: 'delete_utilisateur', methods: ['POST'])]
    public function delete(int $id, EntityManagerInterface $entityManager): RedirectResponse
    {
        $utilisateur = $entityManager->getRepository(Utilisateur::class)->find($id);

        if (!$utilisateur) {
            $this->addFlash('error', 'Utilisateur introuvable.');
            return $this->redirectToRoute('list_utilisateurs');
        }

        $entityManager->remove($utilisateur);
        $entityManager->flush();

        $this->addFlash('success', 'Utilisateur supprimé avec succès.');

        return $this->redirectToRoute('list_utilisateurs');
    }
    #[Route('/utilisateurs/mettre-a-jour/{id}', name: 'update_utilisateur', methods: ['GET', 'POST'])]
public function update(int $id, Request $request, EntityManagerInterface $entityManager): Response
{
    // Trouver l'utilisateur à mettre à jour
    $utilisateur = $entityManager->getRepository(Utilisateur::class)->find($id);

    if (!$utilisateur) {
        $this->addFlash('error', 'Utilisateur introuvable.');
        return $this->redirectToRoute('list_utilisateurs');
    }

    // Créez un formulaire (vous pouvez personnaliser cela avec un formulaire Symfony)
    $form = $this->createForm(UtilisateurType::class, $utilisateur);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager->flush();
        $this->addFlash('success', 'Utilisateur mis à jour avec succès.');
        return $this->redirectToRoute('list_utilisateurs');
    }

    return $this->render('users_list/update_utilisateur.html.twig', [
        'form' => $form->createView(),
    ]);
}

  
}
