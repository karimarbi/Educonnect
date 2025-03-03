<?php
namespace App\Entity;

use App\Repository\TestRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: TestRepository::class)]
class Test
{
#[ORM\Id]
#[ORM\GeneratedValue]
#[ORM\Column(type: 'integer')]
private ?int $id = null;

#[ORM\Column(type: 'string', length: 255)]
private ?string $matiere = null;

#[ORM\Column(type: 'float')]
private ?float $heuredutest = null;

#[ORM\Column(type: 'string', length: 255)]
private ?string $nomduformateur = null;

#[ORM\ManyToOne(targetEntity: Type::class)]
#[ORM\JoinColumn(nullable: false)]
private ?Type $type = null;


    #[ORM\Column(type: 'boolean')]
    private bool $isDeleted = false;

    public function isDeleted(): bool
    {
        return $this->isDeleted;
    }

    public function getIsDeleted(): bool
    {
        return $this->isDeleted;
    }

    public function setDeleted(bool $deleted): self
    {
        $this->isDeleted = $deleted;
        return $this;
    }
// Add this method to fix the issue
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

public function getType(): ?Type
{
return $this->type;
}

public function setType(?Type $type): static
{
$this->type = $type;
return $this;
}
}
