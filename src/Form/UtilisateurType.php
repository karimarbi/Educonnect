<?php
namespace App\Form;

use App\Entity\Utilisateur;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\HiddenType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\FormEvent;
use Symfony\Component\Form\FormEvents;
use Symfony\Component\Validator\Constraints as Assert;

class UtilisateurType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', TextType::class, [
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
            ->add('age', NumberType::class, [
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
                'constraints' => [
                    new Assert\NotBlank(['message' => 'L\'adresse est obligatoire.']),
                ],
            ])
            ->add('lieu', ChoiceType::class, [
                'choices' => [
                    'Tunis' => 'Tunis',
                    'Sfax' => 'Sfax',
                    'Sousse' => 'Sousse',
                    'Ariana' => 'Ariana',
                    'Ben Arous' => 'Ben Arous',
                    'Nabeul' => 'Nabeul',
                    'Kairouan' => 'Kairouan',
                    'Gabès' => 'Gabès',
                    'Monastir' => 'Monastir',
                    'Medenine' => 'Medenine',
                    'Bizerte' => 'Bizerte',
                    'Mahdia' => 'Mahdia',
                    'Jendouba' => 'Jendouba',
                    'Gafsa' => 'Gafsa',
                    'Kasserine' => 'Kasserine',
                    'Tozeur' => 'Tozeur',
                    'Tataouine' => 'Tataouine',
                    'Siliana' => 'Siliana',
                    'Zaghouan' => 'Zaghouan',
                    'Sidi Bouzid' => 'Sidi Bouzid',
                    'Le Kef' => 'Le Kef',
                    'Beja' => 'Beja',
                    'Manouba' => 'Manouba',
                ],
                'placeholder' => 'Choisir une ville',
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Veuillez choisir une ville.']),
                ],
            ])
            ->add('email', EmailType::class, [
                'constraints' => [
                    new Assert\NotBlank(['message' => 'L\'email est obligatoire.']),
                    new Assert\Email(['message' => 'Veuillez entrer une adresse email valide.']),
                ],
            ])
            ->add('password', PasswordType::class, [
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
            ->add('role', ChoiceType::class, [
                'choices' => [
                    'Membre' => 'membre',
                    'Formateur' => 'formateur',
                    'Admin' => 'admin',
                ],
                'constraints' => [
                    new Assert\NotBlank(['message' => 'Veuillez choisir un rôle.']),
                ],
            ])
            ->add('specialite', TextType::class, [
                'required' => false,
                'constraints' => [
                    new Assert\Length([
                        'max' => 100,
                        'maxMessage' => 'La spécialité ne peut pas dépasser {{ limit }} caractères.',
                    ]),
                ],
            ])
            ->add('salaire', NumberType::class, [
                'required' => false,
                'constraints' => [
                    new Assert\Range([
                        'min' => 0,
                        'max' => 10000,
                        'notInRangeMessage' => 'Le salaire doit être compris entre {{ min }} et {{ max }}.',
                    ]),
                ],
            ]);

        $builder->addEventListener(FormEvents::PRE_SUBMIT, function (FormEvent $event) {
            $data = $event->getData();
            $form = $event->getForm();

            if (isset($data['role']) && $data['role'] === 'formateur') {
                $form->add('specialite', TextType::class, [
                    'required' => true,
                    'constraints' => [
                        new Assert\NotBlank(['message' => 'La spécialité est obligatoire pour un formateur.']),
                    ],
                ]);
                $form->add('salaire', NumberType::class, [
                    'required' => true,
                    'constraints' => [
                        new Assert\NotBlank(['message' => 'Le salaire est obligatoire pour un formateur.']),
                    ],
                ]);
            }
        });

        $builder->add('_token', HiddenType::class, ['mapped' => false]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}
