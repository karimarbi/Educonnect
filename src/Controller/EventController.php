<?php

namespace App\Controller;

use App\Entity\Event;
use App\Form\EventType;
use App\Repository\EventRepository;
use Doctrine\ORM\EntityManagerInterface;
use Endroid\QrCode\Label\Font\Font;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\String\Slugger\SluggerInterface;
use Symfony\Component\HttpFoundation\File\File;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel;
use Endroid\QrCode\Writer\PngWriter;



#[Route('/event')]
final class EventController extends AbstractController
{
    #[Route(name: 'app_event_index', methods: ['GET'])]
    public function index(EventRepository $eventRepository): Response
    {
        return $this->render('event/index.html.twig', [
            'events' => $eventRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_event_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $event = new Event();
        $form = $this->createForm(EventType::class, $event);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Gérer l'upload de l'image
            $uploadedFile = $form['imageFile']->getData();
            if ($uploadedFile) {
                $uploadsDirectory = $this->getParameter('uploads_directory');
                $filename = md5(uniqid()) . '.' . $uploadedFile->guessExtension();
                $uploadedFile->move($uploadsDirectory, $filename);
                $event->setImage($filename);
            }

            $entityManager->persist($event);
            $entityManager->flush();

            return $this->redirectToRoute('app_gestion_event', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('event/new.html.twig', [
            'event' => $event,
            'form' => $form->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_event_show', methods: ['GET'])]
    public function show(Event $event): Response
    {
        return $this->render('event/show.html.twig', [
            'event' => $event,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_event_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Event $event, EntityManagerInterface $entityManager): Response
    {
        // Sauvegarde l'image existante si aucune nouvelle n'est ajoutée
        $existingImage = $event->getImage();

        $form = $this->createForm(EventType::class, $event);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Gérer l'upload de l'image si une nouvelle est fournie
            $uploadedFile = $form['imageFile']->getData();
            if ($uploadedFile) {
                $uploadsDirectory = $this->getParameter('uploads_directory');
                $filename = md5(uniqid()) . '.' . $uploadedFile->guessExtension();
                $uploadedFile->move($uploadsDirectory, $filename);
                $event->setImage($filename);
            } else {
                $event->setImage($existingImage);
            }

            $entityManager->flush();

            return $this->redirectToRoute('app_gestion_event', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('event/edit.html.twig', [
            'event' => $event,
            'form' => $form->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_event_delete', methods: ['POST'])]
    public function delete(Request $request, Event $event, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$event->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($event);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_gestion_event', [], Response::HTTP_SEE_OTHER);
    }
    #[Route('/front/event/{id}', name: 'app_event_show_front', methods: ['GET'])]
    public function showFront(Event $event): Response
    {
        return $this->render('event/detailFront.html.twig', [
            'event' => $event,
        ]);
    }

    #[Route('/register/{id}', name: 'app_event_register', methods: ['GET', 'POST'])]
    public function register($id, EntityManagerInterface $em): Response
    {
        $event = $em->getRepository(Event::class)->find($id);

        if (!$event) {
            throw $this->createNotFoundException("L'événement n'existe pas.");
        }

        if ($event->getNombreMaxParticipants() > 0) {
            $event->setNombreMaxParticipants($event->getNombreMaxParticipants() - 1);
            $em->persist($event);
            $em->flush();

            // ✅ Corrected QR Code Generation for Endroid QR Code v6
            $qrCode = new QrCode(
                "Titre: {$event->getTitre()}\n"
                . "Date: {$event->getDateDebut()->format('Y-m-d')} - {$event->getDateFin()->format('Y-m-d')}\n"
                . "Lieu: {$event->getLieu()}\n"
                . "Description: {$event->getDescription()}"
            );

            // ✅ Generate QR code as an image
            $writer = new PngWriter();
            $result = $writer->write($qrCode);

            return new Response($result->getString(), 200, [
                'Content-Type' => 'image/png',
            ]);
        } else {
            $this->addFlash('error', "Plus de places disponibles pour cet événement.");
            return $this->redirectToRoute('app_event_index_front');
        }
    }

}


   

