<?php
// src/Entity/Categorie.php

namespace App\Entity;

use App\Repository\CategorieRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: CategorieRepository::class)]
class Categorie
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)] 
    #[Assert\NotBlank(message: "Le nom de catégorie est obligatoire.")]
    #[Assert\Length(
        min: 3,
        max: 25,
        minMessage: "Le nom de catégorie doit contenir au moins {{ limit }} caractères.",
        maxMessage: "Le nom de catégorie ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $nomCategorie = null;

    #[ORM\OneToMany(targetEntity: Cours::class, mappedBy: 'categorie', cascade: ['remove'])]
    private Collection $cours;

    public function __construct()
    {
        $this->cours = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNomCategorie(): ?string
    {
        return $this->nomCategorie;
    }

    public function setNomCategorie(string $nomCategorie): static
    {
        $this->nomCategorie = $nomCategorie;

        return $this;
    }

    public function getCours(): Collection
    {
        return $this->cours;
    }

    public function addCour(Cours $cour): static
    {
        if (!$this->cours->contains($cour)) {
            $this->cours->add($cour);
            $cour->setCategorie($this);
        }

        return $this;
    }

    public function removeCour(Cours $cour): static
    {
        if ($this->cours->removeElement($cour)) {
            if ($cour->getCategorie() === $this) {
                $cour->setCategorie(null);
            }
        }

        return $this;
    }

    public function __toString(): string
    {
        return $this->nomCategorie ?? 'N/A';
    }
}
