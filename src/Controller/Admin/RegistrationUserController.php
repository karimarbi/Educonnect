<?php

namespace App\Controller\Admin;

use App\Entity\Utilisateur;
use App\Form\UtilisateurType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;

#[Route('/admin')]
class RegistrationUserController extends AbstractController
{
    #[Route('/inscription', name: 'inscription', methods: ['GET', 'POST'])]
    public function inscription(
        Request $request, 
        EntityManagerInterface $entityManager, 
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $utilisateur = new Utilisateur();
        $form = $this->createForm(UtilisateurType::class, $utilisateur);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Vérifier si l'email ou le CIN existent déjà en base de données
            $existingUserByEmail = $entityManager->getRepository(Utilisateur::class)->findOneBy([
                'email' => $utilisateur->getEmail()
            ]);

            $existingUserByCin = $entityManager->getRepository(Utilisateur::class)->findOneBy([
                'cin' => $utilisateur->getCin()
            ]);

            if ($existingUserByEmail) {
                $this->addFlash('danger', 'Cet email est déjà utilisé. Veuillez en choisir un autre.');
            }

            if ($existingUserByCin) {
                $this->addFlash('danger', 'Ce CIN est déjà utilisé. Veuillez en choisir un autre.');
            }

            // Empêcher l'enregistrement si un utilisateur existe déjà avec l'email ou le CIN
            if ($existingUserByEmail || $existingUserByCin) {
                return $this->render('registration/inscription.html.twig', [
                    'form' => $form->createView(),
                ]);
            }

            // Hash du mot de passe avant de l'enregistrer
            $hashedPassword = $passwordHasher->hashPassword($utilisateur, $utilisateur->getPassword());
            $utilisateur->setPassword($hashedPassword);

            // Si ce n'est pas un formateur, on ne garde pas la spécialité ni le salaire
            if ($utilisateur->getRole() !== 'formateur') {
                $utilisateur->setSpecialite(null);
                $utilisateur->setSalaire(null);
            }

            // Enregistrer l'utilisateur
            $entityManager->persist($utilisateur);
            $entityManager->flush();

            $this->addFlash('success', 'Utilisateur ajouté avec succès.');
            return $this->redirectToRoute('list_utilisateurs');
        }

        return $this->render('registration/inscription.html.twig', [
            'form' => $form->createView(),
        ]);
    }
}
