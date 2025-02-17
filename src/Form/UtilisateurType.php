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
       
            ])
            ->add('prenom', TextType::class, [
         
            ])
            ->add('cin', TextType::class, [
        
            ])
            ->add('age', NumberType::class, [
           
            ])
            ->add('dateNai', DateType::class, [
                'widget' => 'single_text',
            
            ])
            ->add('adresse', TextType::class, [
               
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
            ])
            ->add('email', EmailType::class, [
              
            ])
            ->add('mdp', PasswordType::class, [
            
            ])
            ->add('role', ChoiceType::class, [
                'choices' => [
                    'Membre' => 'membre',
                    'Formateur' => 'formateur',
                    'Admin' => 'admin',
                ],
             
            ])
            ->add('specialite', TextType::class, [
                'required' => false,
            ])
            ->add('salaire', NumberType::class, [
                'required' => false,
            
            ]);

        $builder->addEventListener(FormEvents::PRE_SUBMIT, function (FormEvent $event) {
            $data = $event->getData();
            $form = $event->getForm();

            if (isset($data['role']) && $data['role'] === 'formateur') {
                $form->add('specialite', TextType::class, ['required' => true]);
                $form->add('salaire', NumberType::class, ['required' => true]);
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
