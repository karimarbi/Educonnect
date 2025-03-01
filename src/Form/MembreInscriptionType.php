<?php
namespace App\Form;

use App\Entity\Utilisateur;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\TelType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints as Assert;

class MembreInscriptionType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('nom', TextType::class, [
                'label' => 'Nom',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le nom est obligatoire.']),
                    new Assert\Length([
                        'min' => 2,
                        'max' => 50,
                        'minMessage' => 'Le nom doit contenir au moins {{ limit }} caractères.',
                        'maxMessage' => 'Le nom ne peut pas dépasser {{ limit }} caractères.',
                    ]),
                ],
            ])
            ->add('prenom', TextType::class, [
                'label' => 'Prénom',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le prénom est obligatoire.']),
                    new Assert\Length([
                        'min' => 2,
                        'max' => 50,
                        'minMessage' => 'Le prénom doit contenir au moins {{ limit }} caractères.',
                        'maxMessage' => 'Le prénom ne peut pas dépasser {{ limit }} caractères.',
                    ]),
                ],
            ])
            ->add('cin', TextType::class, [
                'label' => 'CIN',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le CIN est obligatoire.']),
                    new Assert\Length([
                        'min' => 8,
                        'max' => 8,
                        'exactMessage' => 'Le CIN doit contenir exactement {{ limit }} chiffres.',
                    ]),
                    new Assert\Regex([
                        'pattern' => '/^\d{8}$/',
                        'message' => 'Le CIN doit être composé uniquement de chiffres.',
                    ]),
                ],
            ])
            ->add('age', TextType::class, [
                'label' => 'Âge',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'L\'âge est obligatoire.']),
                    new Assert\Range([
                        'min' => 18,
                        'max' => 100,
                        'notInRangeMessage' => 'L\'âge doit être compris entre {{ min }} et {{ max }} ans.',
                    ]),
                ],
            ])
            ->add('dateNai', DateType::class, [
                'label' => 'Date de naissance',
                'widget' => 'single_text',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'La date de naissance est obligatoire.']),
                    new Assert\LessThan([
                        'value' => 'today',
                        'message' => 'La date de naissance doit être une date passée.',
                    ]),
                ],
            ])
            ->add('adresse', TextType::class, [
                'label' => 'Adresse',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'L\'adresse est obligatoire.']),
                ],
            ])
            ->add('lieu', TextType::class, [
                'label' => 'Lieu',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le lieu est obligatoire.']),
                ],
            ])
            ->add('email', EmailType::class, [
                'label' => 'Email',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'L\'email est obligatoire.']),
                    new Assert\Email(['message' => 'Veuillez entrer une adresse email valide.']),
                ],
            ])
            ->add('password', PasswordType::class, [
                'label' => 'Mot de passe',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le mot de passe est obligatoire.']),
                    new Assert\Length([
                        'min' => 8,
                        'max' => 255,
                        'minMessage' => 'Le mot de passe doit contenir au moins {{ limit }} caractères.',
                    ]),
                    new Assert\Regex([
                        'pattern' => '/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/',
                        'message' => 'Le mot de passe doit contenir au moins une lettre, un chiffre et un caractère spécial.',
                    ]),
                ],
            ])
            ->add('numtlfn', TelType::class, [
                'label' => 'Numéro de téléphone',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Le numéro de téléphone est obligatoire.']),
                    new Assert\Regex([
                        'pattern' => '/^\+216\d{8}$/',
                        'message' => 'Le numéro de téléphone doit commencer par +216 suivi de 8 chiffres.',
                    ]),
                ],
                'attr' => ['placeholder' => '+216XXXXXXXX'],
            ])
            ->add('image', FileType::class, [
                'label' => 'Photo de profil',
                'required' => false,
                'constraints' => [
                    new Assert\File([
                        'maxSize' => '2M',
                        'mimeTypes' => ['image/jpeg', 'image/png'],
                        'mimeTypesMessage' => 'Veuillez télécharger une image valide (JPEG ou PNG).',
                    ]),
                ],
                'attr' => [
                    'accept' => 'image/jpeg, image/png'
                ]
            ]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}
