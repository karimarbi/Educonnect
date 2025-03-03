<?php
namespace App\Form;

use App\Entity\Test;
use App\Entity\Type;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class TestType extends AbstractType
{
public function buildForm(FormBuilderInterface $builder, array $options): void
{
$builder
->add('matiere')
->add('heuredutest')
->add('nomduformateur')
->add('type', EntityType::class, [
'class' => Type::class,
'choice_label' => 'mode', // Display the "mode" field in dropdown
'placeholder' => 'Sélectionnez un type', // Optional: Adds a placeholder
'required' => true,
]);
}

public function configureOptions(OptionsResolver $resolver): void
{
$resolver->setDefaults([
'data_class' => Test::class,
]);
}
}
