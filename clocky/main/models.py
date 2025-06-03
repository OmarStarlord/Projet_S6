from django.db import models
from django.utils import timezone


class Etudiant(models.Model):
    nom_etudiant = models.CharField(max_length=100,null=True)
    numero_etudiant = models.CharField(max_length=20, unique=True)
    email = models.EmailField(unique=True, null=True)
    mot_de_passe = models.CharField(max_length=100,null=True)
    groupe_tp = models.IntegerField(null=True, blank=True)
    groupe_td = models.IntegerField(null=True, blank=True)
    date_inscription = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return self.nom_etudiant


class Professeur(models.Model):
    nom_professeur = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    mot_de_passe = models.CharField(max_length=100)

    def __str__(self):
        return self.nom_professeur


class Cours(models.Model):
    TYPE_COURS_CHOICES = [
        ('CM', 'Cours Magistral'),
        ('TD1', 'Travaux Dirigés'),
        ('TP1', 'Travaux Pratiques'),
        ('TD2', 'Travaux Dirigés 2'),
        ('TP2', 'Travaux Pratiques 2'),
        ('TD3', 'Travaux Dirigés 3'),
        ('TP3', 'Travaux Pratiques 3'),
    ]

    nom_cours = models.CharField(max_length=100)
    professeur = models.ForeignKey(Professeur, on_delete=models.CASCADE)
    date_cours = models.DateTimeField(default=timezone.now)
    type_cours = models.CharField(max_length=4, choices=TYPE_COURS_CHOICES)

    def __str__(self):
        return f"{self.nom_cours} - {self.get_type_cours_display()} - {self.professeur.nom_professeur}"


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
        return f"{self.etudiant.nom_etudiant} - {self.cours.nom_cours} - {self.statut or 'Non marqué'}"
