<?php

namespace App\Controller;

use App\Repository\UtilisateurRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Knp\Snappy\Pdf;
use Symfony\Component\HttpFoundation\Request;

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

    #[Route('/stats/pdf', name: 'export_stats_pdf', methods: ['GET'])]
    public function exportStatsPdf(UtilisateurRepository $utilisateurRepository, Pdf $knpSnappyPdf): Response
    {
        // 🔍 Récupérer les statistiques
        $statsLieu = $utilisateurRepository->findStatsByLieu();
        $statsAge = $utilisateurRepository->findAgeStats();
        $ageRangeStats = $utilisateurRepository->findUsersGroupedByAgeRange();

        // 🔍 Debugging : Afficher les données dans la console Symfony
        dump($statsLieu, $statsAge, $ageRangeStats);
        
        if (empty($statsLieu) && empty($statsAge) && empty($ageRangeStats)) {
            throw new \Exception("Aucune donnée disponible pour les statistiques.");
        }

        // Générer la vue Twig en HTML
        $html = $this->renderView('stats/stats_pdf.html.twig', [
            'statsLieu'      => $statsLieu,
            'statsAge'       => $statsAge,
            'ageRangeStats'  => $ageRangeStats,
        ]);

        return new Response(
            $knpSnappyPdf->getOutputFromHtml($html),
            200,
            [
                'Content-Type' => 'application/pdf',
                'Content-Disposition' => 'attachment; filename="statistiques_utilisateurs.pdf"',
            ]
        );
    }
}
