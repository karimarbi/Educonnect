<?php

namespace App\Controller;

use App\Repository\CategoryRepository;
use App\Repository\EventRepository;
use App\Repository\TestRepository;
use App\Repository\TypedetestRepository;
use App\Repository\TypeRepository;
use App\Repository\UtilisateurRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/back')] // Préfixe pour toutes les routes de ce contrôleur
final class BackOfficeController extends AbstractController
{
    #[Route('/', name: 'app_back_office', methods: ['GET'])]
    public function index(): Response
    {
        return $this->render('back_office/index.html.twig', [
            'controller_name' => 'BackOfficeController',
        ]);
    }



    // Integration event backOffice
    #[Route('/gestion_event', name: 'app_gestion_event', methods: ['GET'])]
    public function gestionEvent(EventRepository $eventRepository, CategoryRepository $categoryRepository): Response
    {
        // Récupération des événements et des catégories via les repositories
        $events = $eventRepository->findAll();
        $categories = $categoryRepository->findAll();

        // Rendu du template gestion_event.html.twig avec les événements et les catégories
        return $this->render('back_office/gestion_event.html.twig', [
            'events' => $events,
            'categories' => $categories,
        ]);
    }
    #[Route('/gestion_test', name: 'app_gestion_test', methods: ['GET'])]
    public function gestionTest(
        TestRepository $testRepository,
        TypeRepository $typetestRepository
    ): Response {
        // Récupération des tests et des types de tests via les repositories
        $tests = $testRepository->findAll();  // Fetch all tests
        $types = $typetestRepository->findAll();  // Fetch all types of tests

        // Rendu du template gestion_test.html.twig avec les tests et les types de tests
        return $this->render('back_office/gestion_test.html.twig', [
            'tests' => $tests,
            'types' => $types,
        ]);
    }
    
     // Integration gestion user backOffice
     #[Route('/gestion_user', name: 'app_gestion_user', methods: ['GET'])]
     public function gestionUser(UtilisateurRepository $userRepository): Response
     {
         // Récupération des utilisateues via les repositories
         $users = $userRepository->findAll();
         // Rendu du template gestion_user.html.twig 
         return $this->render('back_office/gestion_user.html.twig', [
             'users' => $users,
            
         ]);
     }


}
