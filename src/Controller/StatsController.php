<?php

namespace App\Controller;

use App\Repository\UtilisateurRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class StatsController extends AbstractController
{
    #[Route('/stats', name: 'stats_index',methods: ['GET'])]
    public function index(UtilisateurRepository $utilisateurRepository): Response
    {
        $statsLieu = $utilisateurRepository->findStatsByLieu();
        $statsAge = $utilisateurRepository->findAgeStats();
        $ageRangeStats = $utilisateurRepository->findUsersGroupedByAgeRange();

        return $this->render('stats/index.html.twig', [
            'statsLieu'      => $statsLieu,
            'statsAge'       => $statsAge,
            'ageRangeStats'  => $ageRangeStats,
            'controller_name' => 'StatsController',
        ]);
    }
}
