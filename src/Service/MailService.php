<?php
namespace App\Service;

use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use Twig\Environment;

class MailService
{
    private MailerInterface $mailer;
    private Environment $twig;
    private string $fromEmail;

    public function __construct(MailerInterface $mailer, Environment $twig, string $fromEmail)
    {
        $this->mailer = $mailer;
        $this->twig = $twig;
        $this->fromEmail = $fromEmail;
    }

    public function sendUserCredentials(string $to, string $email, string $mdp)
    {
        $htmlContent = $this->twig->render('email/user_credentials.html.twig', [
            'email' => $email,
            'mdp' => $mdp
        ]);

        $emailMessage = (new Email())
            ->from($this->fromEmail)
            ->to($to)
            ->subject('Votre compte a été créé')
            ->html($htmlContent);

        $this->mailer->send($emailMessage);
    }
}
