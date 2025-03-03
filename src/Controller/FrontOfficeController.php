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
use Knp\Component\Pager\PaginatorInterface;

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

    #[Route('/front/event', name: 'app_event_index_front', methods: ['GET'])]
    public function indexFront(
        EventRepository $eventRepository,
        CategoryRepository $categoryRepository,
        Request $request,
        PaginatorInterface $paginator
    ): Response {
        $categoryId = $request->query->get('category');
        $searchTerm = $request->query->get('search');

        $queryBuilder = $eventRepository->createQueryBuilder('e');

        // Filter by category if provided
        if ($categoryId) {
            $queryBuilder->andWhere('e.category = :category')
                ->setParameter('category', $categoryId);
        }

        // Search by event title or description
        if (!empty($searchTerm)) {
            $queryBuilder->andWhere('e.titre LIKE :search OR e.description LIKE :search')
                ->setParameter('search', '%' . $searchTerm . '%');
        }

        // Get paginated events
        $query = $queryBuilder->getQuery();
        $events = $paginator->paginate($query, $request->query->getInt('page', 1), 6);

        return $this->render('event/indexFront.twig', [
            'events' => $events,
            'categories' => $categoryRepository->findAll(),
            'selectedCategoryId' => $categoryId,
            'searchTerm' => $searchTerm,
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
}
