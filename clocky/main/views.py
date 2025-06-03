from rest_framework import generics, status
from rest_framework.views import APIView
from rest_framework.response import Response

from .models import Etudiant, Cours, Attendance, Professeur
from .serializers import (
    EtudiantCreateSerializer,
    CoursSerializer,
    AttendanceSerializer,
    CustomTokenObtainPairSerializer,
    ProfesseurCreateSerializer,
)



# Créer un étudiant (avec User)
class EtudiantCreateAPIView(generics.CreateAPIView):
    queryset = Etudiant.objects.all()
    serializer_class = EtudiantCreateSerializer
    
class ProfesseurCreateAPIView(generics.CreateAPIView):
    queryset = Professeur.objects.all()
    serializer_class = ProfesseurCreateSerializer


# Créer un cours
class CoursCreateAPIView(generics.CreateAPIView):
    queryset = Cours.objects.all()
    serializer_class = CoursSerializer

# Créer une ligne de présence
class AttendanceCreateAPIView(generics.CreateAPIView):
    queryset = Attendance.objects.all()
    serializer_class = AttendanceSerializer

# Liste des présences pour un cours
class ListePresencesAPIView(APIView):
    def get(self, request, cours_id):
        presences = Attendance.objects.filter(cours_id=cours_id).select_related('etudiant')
        serializer = AttendanceSerializer(presences, many=True)
        return Response(serializer.data)

# Mettre à jour les présences
class SoumettrePresencesAPIView(APIView):
    def post(self, request):
        data = request.data
        if not isinstance(data, list):
            return Response({"detail": "Données invalides"}, status=status.HTTP_400_BAD_REQUEST)

        for item in data:
            attendance_id = item.get("id")
            statut = item.get("statut")
            if attendance_id and statut in ['present', 'absent', 'en_retard']:
                Attendance.objects.filter(id=attendance_id).update(statut=statut)

        return Response({"message": "Présences mises à jour avec succès."})



class EtudiantLoginView(APIView):
    def post(self, request):
        email = request.data.get('email')
        mot_de_passe = request.data.get('mot_de_passe')

        if not email or not mot_de_passe:
            return Response({'error': 'Email et mot de passe requis'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            etudiant = Etudiant.objects.get(email=email)
            if etudiant.mot_de_passe == mot_de_passe:
                return Response({'message': 'Connexion réussie', 'id': etudiant.id})
            else:
                return Response({'error': 'Mot de passe incorrect'}, status=status.HTTP_401_UNAUTHORIZED)
        except Etudiant.DoesNotExist:
            return Response({'error': 'Étudiant non trouvé'}, status=status.HTTP_404_NOT_FOUND)


class ProfesseurLoginView(APIView):
    def post(self, request):
        email = request.data.get('email')
        mot_de_passe = request.data.get('mot_de_passe')

        if not email or not mot_de_passe:
            return Response({'error': 'Email et mot de passe requis'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            prof = Professeur.objects.get(email=email)
            if prof.mot_de_passe == mot_de_passe:
                return Response({'message': 'Connexion réussie', 'id': prof.id})
            else:
                return Response({'error': 'Mot de passe incorrect'}, status=status.HTTP_401_UNAUTHORIZED)
        except Professeur.DoesNotExist:
            return Response({'error': 'Professeur non trouvé'}, status=status.HTTP_404_NOT_FOUND)
        

