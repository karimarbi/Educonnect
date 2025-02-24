<?php

namespace App\Controller\Admin;

use App\Entity\Utilisateur;
use App\Form\UtilisateurType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Service\MailService;


#[Route('/admin')]
class RegistrationUserController extends AbstractController
{
    #[Route('/inscription', name: 'inscription', methods: ['GET', 'POST'])]
    public function inscription(Request $request, EntityManagerInterface $entityManager,MailService $mailService): Response
    {
        $utilisateur = new Utilisateur();
        $form = $this->createForm(UtilisateurType::class, $utilisateur);

        $form->handleRequest($request);

        // Debug : Vérifier les données envoyées
        dump($request->request->all());

        if ($form->isSubmitted()) {
            dump('Formulaire soumis');

            if ($form->isValid()) {
                dump('Formulaire valide');

                if ($utilisateur->getRole() !== 'formateur') {
                    $utilisateur->setSpecialite(null);
                    $utilisateur->setSalaire(null);
                }

                $entityManager->persist($utilisateur);
                $entityManager->flush();

                 //  Envoyer l'e-mail avec le mot de passe 
                 $mailService->sendUserCredentials($utilisateur->getEmail(), $utilisateur->getEmail(), $utilisateur->getMdp());

                $this->addFlash('success', 'Utilisateur ajouté avec succès.');
                return $this->redirectToRoute('list_utilisateurs');

            } else {
                dump('Formulaire invalide');

                // Afficher les erreurs du formulaire
                dump($form->getErrors(true, false));
            }
        }

        return $this->render('registration/inscription.html.twig', [
            'form' => $form->createView(),
        ]);
    }
}
