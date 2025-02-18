<?php
namespace App\Form;
// src/Form/CategorieType.php
// src/Form/CategorieType.php


use App\Entity\Categorie;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;

class CategorieType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('nomCategorie', TextType::class, [
                'label' => 'Nom de la Catégorie',
                'attr' => ['class' => 'form-control'],
                'constraints' => [
                    new Length([
                        'min' => 3,
                        'max' => 8,
                        'minMessage' => 'Le nom de la catégorie doit comporter au moins {{ limit }} caractères.',
                        'maxMessage' => 'Le nom de la catégorie ne peut pas dépasser {{ limit }} caractères.'
                    ])
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => Categorie::class,  // Ici, tu l'associes à l'entité Categorie
        ]);
    }
}

