<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

class SecurityController extends AbstractController
{
    #[Route('/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils): Response
    {
        if ($this->getUser()) {
            return new RedirectResponse($this->generateUrl('redirect'));
        }

        return $this->render('security/login.html.twig', [
            'last_username' => $authenticationUtils->getLastUsername(),
            'error' => $authenticationUtils->getLastAuthenticationError(),
        ]);
    }

    #[Route('/redirect', name: 'redirect')]
    public function redirectUser(): RedirectResponse
    {
        $user = $this->getUser();
        if (!$user) {
            return new RedirectResponse($this->generateUrl('app_login'));
        }

        $roles = $user->getRoles();
        $role = $roles[0] ?? '';

        return match ($role) {
            'admin' => new RedirectResponse($this->generateUrl('admin_dashboard')),
            'formateur' => new RedirectResponse($this->generateUrl('formateur_dashboard')),
            default => new RedirectResponse($this->generateUrl('homepage')),
        };
    }

    #[Route('/logout', name: 'app_logout')]
    public function logout(): void
    {
        throw new \LogicException('This method can be blank - it will be intercepted by the logout key on your firewall.');
    }
}
