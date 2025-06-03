from django.contrib.auth.models import AbstractUser
from django.db import models
from django.utils import timezone


class User(AbstractUser):
    ROLE_CHOICES = [
        ('etudiant', 'Etudiant'),
        ('professeur', 'Professeur'),
        ('admin', 'Administrateur'),
    ]

    email = models.EmailField(unique=True)
    role = models.CharField(max_length=20, choices=ROLE_CHOICES)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['username']  # username still required internally

    def __str__(self):
        return f"{self.username} ({self.role})"


class Etudiant(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name="etudiant")
    numero_etudiant = models.CharField(max_length=20, unique=True)
    groupe_tp = models.IntegerField(null=True, blank=True)
    groupe_td = models.IntegerField(null=True, blank=True)
    date_inscription = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return self.user.username


class Cours(models.Model):
    TYPE_COURS_CHOICES = [
        ('CM', 'Cours Magistral'),
        ('TD', 'Travaux Dirigés'),
        ('TP', 'Travaux Pratiques'),
    ]

    nom_cours = models.CharField(max_length=100)
    professeur = models.ForeignKey(User, on_delete=models.CASCADE, limit_choices_to={'role': 'professeur'})
    date_cours = models.DateTimeField(default=timezone.now)
    type_cours = models.CharField(max_length=2, choices=TYPE_COURS_CHOICES)
    groupe = models.IntegerField(null=True, blank=True)

    def __str__(self):
        return f"{self.nom_cours} ({self.type_cours} {self.groupe or 'tous'})"


class Attendance(models.Model):
    STATUT_CHOICES = [
        ('present', 'Présent'),
        ('absent', 'Absent'),
        ('en_retard', 'En retard'),
    ]

    cours = models.ForeignKey(Cours, on_delete=models.CASCADE, related_name='presences')
    etudiant = models.ForeignKey(Etudiant, on_delete=models.CASCADE, related_name='presences')
    statut = models.CharField(max_length=10, choices=STATUT_CHOICES, null=True, blank=True)

    class Meta:
        unique_together = ('cours', 'etudiant')

    def __str__(self):
        return f"{self.etudiant.user.username} - {self.cours.nom_cours} - {self.statut or 'Non marqué'}"
