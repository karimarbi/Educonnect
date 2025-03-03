<?php

namespace App\Controller;

use App\Entity\Category;
use App\Entity\Event;
use App\Repository\CategoryRepository;
use App\Repository\EventRepository;
use App\Repository\TestRepository;
use App\Repository\TypeRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use App\Entity\Utilisateur;
use App\Form\MembreInscriptionType;
use Symfony\Component\Notifier\NotifierInterface;
use Symfony\Component\Notifier\Message\SmsMessage;
use Symfony\Component\Notifier\TexterInterface;
final class FrontOfficeController extends AbstractController
{
    // Route pour la page d'accueil
    #[Route('/', name: 'app_front_office', methods: ['GET'])]
    public function index(): Response
    {
        return $this->render('front_office/index.html.twig', [
            'controller_name' => 'FrontOfficeController',
        ]);
    }

    // Route pour afficher les événements avec un filtrage optionnel par catégorie
    #[Route('/front/event', name: 'app_event_index_front', methods: ['GET'])]
    public function indexFront(EventRepository $eventRepository, CategoryRepository $categoryRepository, Request $request): Response
    {
        // Récupère l'ID de la catégorie depuis les paramètres de la requête (GET)
        $categoryId = $request->query->get('category');

        // Si un ID de catégorie est spécifié, filtre les événements par catégorie
        if ($categoryId) {
            $events = $eventRepository->findBy(['category' => $categoryId]);
        } else {
            // Sinon, récupère tous les événements
            $events = $eventRepository->findAll();
        }

        // Récupère toutes les catégories pour les afficher dans le filtre
        $categories = $categoryRepository->findAll();

        return $this->render('event/indexFront.twig', [
            'events' => $events,
            'categories' => $categories,
            'selectedCategoryId' => $categoryId,
        ]);
    }
    #[Route('/front/test', name: 'app_test_index_front', methods: ['GET'])]
    public function indexFrontEvent(TestRepository $testRepository, TypeRepository $typeRepository): Response
    {
        // Retrieve all test records
        $tests = $testRepository->findAll();

        // Retrieve all type records
        $types = $typeRepository->findAll();

        return $this->render('test/indexFront.html.twig', [
            'tests' => $tests,
            'types' => $types,
        ]);
    }

    #[Route('/inscriptionMembre', name: 'app_inscription_front', methods: ['GET', 'POST'])]
    public function inscription(Request $request, EntityManagerInterface $entityManager, UserPasswordHasherInterface $passwordHasher, TexterInterface $texter): Response
    {
        $utilisateur = new Utilisateur();
        $form = $this->createForm(MembreInscriptionType::class, $utilisateur);
    
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            // Hacher le mot de passe
            $hashedPassword = $passwordHasher->hashPassword($utilisateur, $utilisateur->getPassword());
            $utilisateur->setPassword($hashedPassword);
    
            // Définir le rôle par défaut
            $utilisateur->setRole('membre');
    
            // Sauvegarder en base de données
            $entityManager->persist($utilisateur);
            $entityManager->flush();
            
            if ($utilisateur->getNumtlfn()) {
                $sms = new SmsMessage(
                    $utilisateur->getNumtlfn(), // Numéro du membre
                    'Bonjour ' . $utilisateur->getPrenom() . ', votre inscription sur EduConnect a été réussie ! 🎉'
                );
    
                $texter->send($sms); // ✅ Envoi correct via TexterInterface
            }
    
            // Redirection après l'inscription (ajout du point-virgule)
            return $this->redirectToRoute('app_inscription_front');
        }
    
        return $this->render('registration/Front_inscription.html.twig', [
            'form' => $form->createView(),
        ]);
    }
}
    