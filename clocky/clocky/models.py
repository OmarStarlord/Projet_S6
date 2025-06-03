# models.py 
from django.db import models
from django.utils import timezone



class Etudiant(models.Model):
    nom_etudiant = models.CharField(max_length=100)
    numero_etudiant = models.CharField(max_length=20, unique=True)
    email = models.EmailField(unique=True)
    mot_de_passe = models.CharField(max_length=100)
    date_inscription = models.DateTimeField(default=timezone.now)
    groupe_tp = models.IntegerField(null=True, blank=True)
    groupe_td = models.IntegerField(null=True, blank=True)

    def __str__(self):
        return self.nom_etudiant


class Professeur(models.Model):
    nom_professeur = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    mot_de_passe = models.CharField(max_length=100)

    def __str__(self):
        return self.nom_professeur


class Admin(models.Model):
    nom_admin = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    mot_de_passe = models.CharField(max_length=100)

    def __str__(self):
        return self.nom_admin




class Cours(models.Model):
    TYPE_COURS_CHOICES = [
        ('CM', 'Cours Magistral'),
        ('TD', 'Travaux Dirigés'),
        ('TP', 'Travaux Pratiques'),
    ]

    nom_cours = models.CharField(max_length=100)
    professeur = models.ForeignKey(Professeur, on_delete=models.CASCADE)
    date_cours = models.DateTimeField(default=timezone.now)
    type_cours = models.CharField(max_length=2, choices=TYPE_COURS_CHOICES)
    groupe = models.IntegerField(null=True, blank=True)  
    etudiants = models.ManyToManyField('Etudiant', through='Attendance', related_name='cours')

    def __str__(self):
        if self.type_cours == 'CM':
            return f"{self.nom_cours} (CM)"
        return f"{self.nom_cours} ({self.type_cours} {self.groupe})"


class Attendance(models.Model):
    STATUT_CHOICES = [
        ('present', 'Présent'),
        ('absent', 'Absent'),
        ('en retard', 'En Retard')
    ]

    cours = models.ForeignKey(Cours, on_delete=models.CASCADE, related_name='presences')
    etudiant = models.ForeignKey(Etudiant, on_delete=models.CASCADE, related_name='presences')
    statut = models.CharField(max_length=10, choices=STATUT_CHOICES, null=True, blank=True)

    class Meta:
        unique_together = ('cours', 'etudiant')

    def __str__(self):
        return f"{self.etudiant.nom_etudiant} - {self.cours} - {self.statut or 'Non marqué'}"
