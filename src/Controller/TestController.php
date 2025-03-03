<?php

namespace App\Controller;

use App\Entity\Test;
use App\Form\TestType;
use App\Repository\TestRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use TCPDF;

#[Route('/test')]
final class TestController extends AbstractController
{
    #[Route(name: 'app_test_index', methods: ['GET'])]
    public function index(TestRepository $testRepository): Response
    {
        return $this->render('test/index.html.twig', [
            'tests' => $testRepository->findBy(['isDeleted' => false]), // Show only non-deleted tests
            'deleted_tests' => $testRepository->findBy(['isDeleted' => true]), // Show deleted tests
        ]);
    }



    #[Route('/new', name: 'app_test_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $test = new Test();
        $form = $this->createForm(TestType::class, $test);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($test);
            $entityManager->flush();

            return $this->redirectToRoute('app_gestion_test', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('test/new.html.twig', [
            'test' => $test,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_test_show', methods: ['GET'])]
    public function show(Test $test): Response
    {
        return $this->render('test/show.html.twig', [
            'test' => $test,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_test_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Test $test, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(TestType::class, $test);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_gestion_test', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('test/edit.html.twig', [
            'test' => $test,
            'form' => $form,
        ]);
    }
    #[Route('/{id}', name: 'app_test_delete', methods: ['POST'])]
    public function delete(Request $request, Test $test, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$test->getId(), $request->getPayload()->getString('_token'))) {
            // Instead of deleting, mark it as deleted
            $test->setDeleted(true);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_gestion_test', [], Response::HTTP_SEE_OTHER);
    }
    #[Route('/deleted', name: 'app_test_deleted', methods: ['GET'])]
    public function deletedTests(TestRepository $testRepository): Response
    {
        return $this->render('test/deleted.html.twig', [
            'deleted_tests' => $testRepository->findBy(['isDeleted' => true]),
        ]);
    }


    #[Route('/export/pdf', name: 'export_test_to_pdf', methods: ['GET'])]
    public function exportTestsToPdf(TestRepository $testRepository): Response
    {
        $tests = $testRepository->findAll();

        $pdf = new TCPDF(PDF_PAGE_ORIENTATION, PDF_UNIT, PDF_PAGE_FORMAT, true, 'UTF-8', false);
        $pdf->SetCreator("EduConnect");
        $pdf->SetAuthor('EduConnect');
        $pdf->SetTitle('Liste des Tests');

        // ** Set Header Data with Logo, Title, and Date **
        $currentDate = date('d-m-Y'); // Format: DD-MM-YYYY
        $pdf->setHeaderData(
            PDF_HEADER_LOGO,  // Logo
            PDF_HEADER_LOGO_WIDTH,  // Logo Width
            "Liste des Tests",  // Title
            "Generated by EduConnect\nDate: $currentDate" // Subtitle
        );

        // ** Set Fonts & Header/Footer Styles **
        $pdf->setHeaderFont([PDF_FONT_NAME_MAIN, '', 14]);
        $pdf->setFooterFont([PDF_FONT_NAME_DATA, '', 10]);

        // ** Margins & Page Breaks **
        $pdf->SetMargins(15, 30, 15);
        $pdf->SetHeaderMargin(10);
        $pdf->SetFooterMargin(15);
        $pdf->SetAutoPageBreak(true, 25);

        $pdf->SetFont('dejavusans', '', 12);
        $pdf->AddPage();

        // ** Render PDF HTML Template **
        $html = $this->renderView('test/pdf.html.twig', [
            'tests' => $tests,
        ]);

        $pdf->writeHTML($html, true, false, true, false, '');

        return new Response($pdf->Output('tests.pdf', 'I'), 200, [
            'Content-Type' => 'application/pdf',
        ]);
    }
    /*#[Route('/statistics', name: 'app_test_statistics', methods: ['GET'])]
    public function testStatistics(TestRepository $testRepository): Response
    {
        $tests = $testRepository->findAll();

        $statistics = [];

        foreach ($tests as $test) {
            $type = $test->getType(); // Assurez-vous que votre entité Test possède un champ "type"
            if (!isset($statistics[$type])) {
                $statistics[$type] = 0;
            }
            $statistics[$type]++;
        }

        return $this->render('test/statistics.html.twig', [
            'statistics' => $statistics,
            'totalTests' => count($tests),
        ]);
    }
*/
    #[Route('/{id}/restore', name: 'app_test_restore', methods: ['POST'])]
    public function restore(Test $test, EntityManagerInterface $entityManager): Response
    {
        if ($test->getIsDeleted()) { // Ensure only deleted tests are restored
            $test->setDeleted(false);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_test_index', [], Response::HTTP_SEE_OTHER);
    }




}
