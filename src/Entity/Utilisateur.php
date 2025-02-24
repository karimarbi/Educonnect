<?php

namespace App\Entity;

use App\Repository\UtilisateurRepository;
use Doctrine\ORM\Mapping as ORM;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
#[ORM\Entity(repositoryClass: UtilisateurRepository::class)]
class Utilisateur
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    private ?string $nom = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;

        return $this;
    }





#[ORM\OneToMany(targetEntity: Inscription::class, mappedBy: 'utilisateur', cascade: ['remove'])]
private Collection $inscriptions;

public function __construct()
{
    $this->inscriptions = new ArrayCollection();
}

public function getInscriptions(): Collection
{
    return $this->inscriptions;
}

public function addInscription(Inscription $inscription): static
{
    if (!$this->inscriptions->contains($inscription)) {
        $this->inscriptions->add($inscription);
        $inscription->setUtilisateur($this);
    }

    return $this;
}

public function removeInscription(Inscription $inscription): static
{
    if ($this->inscriptions->removeElement($inscription)) {
        if ($inscription->getUtilisateur() === $this) {
            $inscription->setUtilisateur(null);
        }
    }

    return $this;
}

}
