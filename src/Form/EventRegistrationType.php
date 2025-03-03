<?php
namespace App\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\OptionsResolver\OptionsResolver;

class EventRegistrationType extends AbstractType
{
public function buildForm(FormBuilderInterface $builder, array $options)
{
$builder
->add('name', TextType::class, [
'label' => 'Nom et Prénom',
'required' => true,
])
->add('email', TextType::class, [
'label' => 'Email',
'required' => true,
])
->add('submit', SubmitType::class, [
'label' => 'S\'inscrire',
]);
}

public function configureOptions(OptionsResolver $resolver)
{
$resolver->setDefaults([]);
}
}
