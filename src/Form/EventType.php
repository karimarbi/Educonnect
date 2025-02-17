<?php

namespace App\Form;

use App\Entity\Event;
use App\Entity\Category;  // Don't forget to import the Category entity
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\EntityType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints as Assert;

class EventType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('titre', TextType::class, [
                'label' => 'Titre de l\'événement',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le titre ne peut pas être vide.']),
                    new Assert\Length([
                        'max' => 50,
                        'maxMessage' => 'Le titre ne peut pas dépasser 50 caractères.',
                    ]),
                ],
            ])
            ->add('dateDebut', DateType::class, [
                'label' => 'Date de début',
                'widget' => 'single_text',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Veuillez sélectionner une date de début.']),
                    new Assert\GreaterThanOrEqual([
                        'value' => (new \DateTime())->format('Y-m-d'),
                        'message' => 'La date de début ne peut pas être dans le passé.',
                    ]),
                ],
            ])
            ->add('dateFin', DateType::class, [
                'label' => 'Date de fin',
                'widget' => 'single_text',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Veuillez sélectionner une date de fin.']),
                ],
            ])
            ->add('lieu', TextType::class, [
                'label' => 'Lieu',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le lieu est obligatoire.']),
                    new Assert\Length([
                        'max' => 100,
                        'maxMessage' => 'Le lieu ne peut pas dépasser 100 caractères.',
                    ]),
                ],
            ])
            ->add('description', TextareaType::class, [
                'label' => 'Description',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La description est obligatoire.']),
                    new Assert\Length([
                        'max' => 500,
                        'maxMessage' => 'La description ne peut pas dépasser 500 caractères.',
                    ]),
                ],
            ])
            ->add('duree', IntegerType::class, [
                'label' => 'Durée (en heures)',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La durée est obligatoire.']),
                    new Assert\Positive(['message' => 'La durée doit être un nombre positif.']),
                ],
            ])
            ->add('nombreMaxParticipants', IntegerType::class, [
                'label' => 'Nombre maximal de participants',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Veuillez indiquer un nombre maximum de participants.']),
                    new Assert\Positive(['message' => 'Le nombre de participants doit être positif.']),
                    new Assert\LessThanOrEqual([
                        'value' => 500,
                        'message' => 'Le nombre de participants ne peut pas dépasser 500.',
                    ]),
                ],
            ])
            ->add('imageFile', FileType::class, [
                'label' => 'Image de l\'événement',
                'mapped' => false, 
                'required' => false,
                'constraints' => [
                    new Assert\File([
                        'maxSize' => '2M',
                        'mimeTypes' => ['image/jpeg', 'image/png'],
                        'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPEG ou PNG, max 2MB).',
                    ]),
                ],
            ])
            ->add('category');
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Event::class,
        ]);
    }
}
