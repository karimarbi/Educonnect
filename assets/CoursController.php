<?php

namespace App\Controller;

use App\Entity\Cours;
use App\Form\Cours1Type;
use App\Repository\CoursRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/cours')]
class CoursController extends AbstractController
{
    #[Route('/', name: 'app_cours_index', methods: ['GET'])]
    public function index(CoursRepository $coursRepository): Response
    {
        return $this->render('cours/index.html.twig', [
            'cours' => $coursRepository->findAll(),
        ]);
    }
    #[Route('/front', name: 'app_cours_indexf', methods: ['GET'])]
    public function indexf(CoursRepository $coursRepository): Response
    {
        return $this->render('cours/indexf.html.twig', [
            'cours' => $coursRepository->findAll(),
        ]);
    }

  
    #[Route('/new', name: 'app_cours_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $cour = new Cours();
        $form = $this->createForm(Cours1Type::class, $cour);
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            // Récupérer le fichier téléchargé
            $imageFile = $form->get('image')->getData();
    
            if ($imageFile) {
                // Utiliser le nom original du fichier sans modifier
                $newFilename = $imageFile->getClientOriginalName();
    
                // Déplacer le fichier dans le répertoire de téléchargement
                try {
                    $imageFile->move(
                        $this->getParameter('cours_images_directory'), // Répertoire d'upload configuré dans services.yaml
                        $newFilename
                    );
                } catch (FileException $e) {
                    // Gérer les erreurs d'upload
                    // Vous pouvez ajouter un message d'erreur ici si nécessaire
                }
    
                // Mettre à jour le champ image avec le nom du fichier
                $cour->setImage($newFilename);
            }
    
            $entityManager->persist($cour);
            $entityManager->flush();
            // $this->addFlash('success', 'Cours ajouté avec succès !');

            return $this->redirectToRoute('app_cours_index', [], Response::HTTP_SEE_OTHER);
        }
    
        return $this->renderForm('cours/new.html.twig', [
            'cour' => $cour,
            'form' => $form,
        ]);
    }
    
    #[Route('/{id}', name: 'app_cours_show', methods: ['GET'])]
    public function show(Cours $cour): Response
    {
        return $this->render('cours/show.html.twig', [
            'cour' => $cour,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_cours_edit', methods: ['GET', 'POST'])]
    // public function edit(Request $request, Cours $cour, EntityManagerInterface $entityManager): Response
    // {
    //     $form = $this->createForm(Cours1Type::class, $cour);
    //     $form->handleRequest($request);

    //     if ($form->isSubmitted() && $form->isValid()) {
    //         // Vérifiez si une nouvelle image a été envoyée
    //         $imageFile = $form->get('image')->getData();
    //         if ($imageFile) {
    //             $newFilename = $imageFile->getClientOriginalName();
    //             try {
    //                 $imageFile->move(
    //                     $this->getParameter('cours_images_directory'),
    //                     $newFilename
    //                 );
    //                 $cour->setImage($newFilename); // Mettre à jour l'image
    //             } catch (FileException $e) {
    //                 // Gérer l'erreur d'upload
    //             }
    //         }
            
    //         $entityManager->flush(); // Mettre à jour la base de données
    //         return $this->redirectToRoute('app_cours_index', [], Response::HTTP_SEE_OTHER);
    //     }
        

    //     return $this->renderForm('cours/edit.html.twig', [
    //         'cour' => $cour,
    //         'form' => $form,
    //     ]);
    // }
    public function edit(Request $request, Cours $cour, EntityManagerInterface $entityManager): Response
    {
        // Create the form
        $form = $this->createForm(Cours1Type::class, $cour);
        $form->handleRequest($request);

        // Check if the form is submitted and valid
        if ($form->isSubmitted()) {
            if ($form->isValid()) {
                // Handle image upload if present
                $imageFile = $form->get('image')->getData();
    
                if ($imageFile) {
                    $newFilename = $imageFile->getClientOriginalName();
    
                    // Try to move the uploaded file
                    try {
                        $imageFile->move(
                            $this->getParameter('cours_images_directory'),
                            $newFilename
                        );
                    } catch (FileException $e) {
                        // Handle the error in case file upload fails
                        $this->addFlash('danger', 'Erreur lors du téléchargement de l\'image.');
                    }

                    // Set the new filename for the image
                    $cour->setImage($newFilename);
                }

                // Save the course entity
                $entityManager->flush();

                // Add success message and redirect
                $this->addFlash('success', 'Le cours a été mis à jour avec succès.');
                return $this->redirectToRoute('app_cours_index');
            } else {
                // Add flash message in case of form errors
                $this->addFlash('danger', 'Il y a des erreurs dans le formulaire.');
            }
        }

        // Render the edit form
        return $this->render('cours/edit.html.twig', [
            'form' => $form->createView(),
            'cour' => $cour,
        ]);
    }
    #[Route('/{id}', name: 'app_cours_delete', methods: ['POST'])]
    public function delete(Request $request, Cours $cour, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$cour->getId(), $request->request->get('_token'))) {
            $entityManager->remove($cour);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_cours_index', [], Response::HTTP_SEE_OTHER);
    }
}
