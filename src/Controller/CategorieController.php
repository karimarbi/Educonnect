<?php

namespace App\Controller;

use App\Entity\Categorie;
use App\Form\CategorieType;
use App\Repository\CategorieRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
#[Route('/categorie')]
class CategorieController extends AbstractController
{






    #[Route('/export-categories', name: 'app_categorie_export')]
    public function exportCategories(EntityManagerInterface $entityManager): Response
    {
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();

        // En-têtes de colonnes
        $sheet->setCellValue('A1', 'ID');
        $sheet->setCellValue('B1', 'Nom de la Catégorie');

        // Récupérer les catégories depuis la base de données
        $categories = $entityManager->getRepository(Categorie::class)->findAll();
        
        $row = 2;
        foreach ($categories as $category) {
            $sheet->setCellValue('A' . $row, $category->getId());
            $sheet->setCellValue('B' . $row, $category->getNomCategorie());
            $row++;
        }

        // Créer le fichier Excel
        $writer = new Xlsx($spreadsheet);
        $fileName = 'categories.xlsx';

        // Réponse HTTP avec le fichier Excel en téléchargement
        $response = new Response();
        $response->headers->set('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        $response->headers->set('Content-Disposition', 'attachment; filename="' . $fileName . '"');

        ob_start();
        $writer->save('php://output');
        $content = ob_get_clean();
        $response->setContent($content);

        return $response;
    }


    #[Route('/', name: 'app_categorie_index', methods: ['GET'])]
    public function index(CategorieRepository $categorieRepository): Response
    {
        return $this->render('categorie/index.html.twig', [
            'categories' => $categorieRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_categorie_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $categorie = new Categorie();
        $form = $this->createForm(CategorieType::class, $categorie);

        $form->handleRequest($request);

        if ($form->isSubmitted()) {
            if ($form->isValid()) {
                // Save the category to the database
                $entityManager->persist($categorie);
                $entityManager->flush();

                // Flash success message
                $this->addFlash('success', 'Catégorie ajoutée avec succès.');

                return $this->redirectToRoute('app_categorie_index');
            } else {
                // Flash error message
                $this->addFlash('danger', 'Erreur dans le formulaire. Veuillez vérifier vos données.');
            }
        }

        return $this->render('categorie/new.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_categorie_show', methods: ['GET'])]
    public function show(Categorie $categorie): Response
    {
        return $this->render('categorie/show.html.twig', [
            'categorie' => $categorie,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_categorie_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Categorie $categorie, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CategorieType::class, $categorie);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_categorie_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('categorie/edit.html.twig', [
            'categorie' => $categorie,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_categorie_delete', methods: ['POST'])]
    public function delete(Request $request, Categorie $categorie, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$categorie->getId(), $request->request->get('_token'))) {
            $entityManager->remove($categorie);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_categorie_index', [], Response::HTTP_SEE_OTHER);
    }
    
}
