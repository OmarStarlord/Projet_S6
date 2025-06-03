
from django.contrib import admin
from .models import Etudiant, Professeur, Cours, Attendance

@admin.register(Etudiant)
class EtudiantAdmin(admin.ModelAdmin):
    list_display = ('nom_etudiant', 'numero_etudiant', 'email', 'groupe_td', 'groupe_tp')

@admin.register(Professeur)
class ProfesseurAdmin(admin.ModelAdmin):
    list_display = ('nom_professeur', 'email')

@admin.register(Cours)
class CoursAdmin(admin.ModelAdmin):
    list_display = ('nom_cours', 'professeur', 'date_cours', 'type_cours')

@admin.register(Attendance)
class AttendanceAdmin(admin.ModelAdmin):
    list_display = ('cours', 'etudiant', 'statut')
