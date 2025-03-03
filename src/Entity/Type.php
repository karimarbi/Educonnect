<?php

namespace App\Entity;

use App\Repository\TypeRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: TypeRepository::class)]
class Type
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: 'integer')]
    private ?int $id;

    #[ORM\Column(type: 'string', length: 255)]
    private ?string $mode = null;
    #[ORM\OneToMany(targetEntity: Test::class, mappedBy: "type")]
    private $tests;
    public function __construct()
    {
        $this->tests = new ArrayCollection();
    }

    public function getTests(): Collection
    {
        return $this->tests;
    }

    public const MODES = ['Présentiel' => 'présentiel', 'En ligne' => 'en_ligne'];

    public function getId(): ?int
    {
       return $this->id;
    }

    public function getMode(): ?string
    {
        return $this->mode;
    }

    public function setMode(string $mode): self
    {
        $this->mode = $mode;

        return $this;
    }

}
