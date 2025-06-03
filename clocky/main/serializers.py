from .models import *
from rest_framework import serializers

class EtudiantCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Etudiant
        fields = ['id', 'nom_etudiant', 'numero_etudiant', 'email', 'mot_de_passe', 'groupe_td', 'groupe_tp']

class ProfesseurCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Professeur
        fields = ['id', 'nom_professeur', 'email', 'mot_de_passe']

class AdminCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Admin
        fields = ['id', 'nom_admin', 'email', 'mot_de_passe']

class CoursSerializer(serializers.ModelSerializer):
    class Meta:
        model = Cours
        fields = ['id', 'nom_cours', 'professeur', 'date_cours', 'type_cours', 'groupe']

class AttendanceSerializer(serializers.ModelSerializer):
    etudiant_nom = serializers.CharField(source='etudiant.nom_etudiant', read_only=True)
    etudiant_id = serializers.IntegerField(source='etudiant.id', read_only=True)

    class Meta:
        model = Attendance
        fields = ['id', 'etudiant_id', 'etudiant_nom', 'statut']
