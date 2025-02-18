<?php

namespace App\Entity;

use App\Repository\TestRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: TestRepository::class)]
class Test
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    private ?string $matiere = null;

    #[ORM\Column]
    private ?float $heuredutest = null;

    #[ORM\Column(length: 255)]
    private ?string $nomduformateur = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getMatiere(): ?string
    {
        return $this->matiere;
    }

    public function setMatiere(string $matiere): static
    {
        $this->matiere = $matiere;

        return $this;
    }

    public function getHeuredutest(): ?float
    {
        return $this->heuredutest;
    }

    public function setHeuredutest(float $heuredutest): static
    {
        $this->heuredutest = $heuredutest;

        return $this;
    }

    public function getNomduformateur(): ?string
    {
        return $this->nomduformateur;
    }

    public function setNomduformateur(string $nomduformateur): static
    {
        $this->nomduformateur = $nomduformateur;

        return $this;
    }
}
