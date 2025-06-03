from rest_framework import generics
from .models import Etudiant, Professeur, Admin, Cours, Attendance
from .serializers import (
    EtudiantCreateSerializer,
    ProfesseurCreateSerializer,
    AdminCreateSerializer,
    CoursSerializer,
    AttendanceSerializer,
)
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status



class EtudiantCreateAPIView(generics.CreateAPIView):
    queryset = Etudiant.objects.all()
    serializer_class = EtudiantCreateSerializer


class ProfesseurCreateAPIView(generics.CreateAPIView):
    queryset = Professeur.objects.all()
    serializer_class = ProfesseurCreateSerializer



class AdminCreateAPIView(generics.CreateAPIView):
    queryset = Admin.objects.all()
    serializer_class = AdminCreateSerializer


class CoursCreateAPIView(generics.CreateAPIView):
    queryset = Cours.objects.all()
    serializer_class = CoursSerializer


class AttendanceCreateAPIView(generics.CreateAPIView):
    queryset = Attendance.objects.all()
    serializer_class = AttendanceSerializer





class LoginAPIView(APIView):
    def post(self, request):
        email = request.data.get("email")
        mot_de_passe = request.data.get("mot_de_passe")
        role = request.data.get("role") 

        if not email or not mot_de_passe or not role:
            return Response({"detail": "Champs manquants."}, status=status.HTTP_400_BAD_REQUEST)

        model = {"etudiant": Etudiant, "professeur": Professeur, "admin": Admin}.get(role.lower())
        if model is None:
            return Response({"detail": "Rôle invalide."}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = model.objects.get(email=email)
            if user.mot_de_passe != mot_de_passe:
                return Response({"detail": "Mot de passe incorrect."}, status=status.HTTP_401_UNAUTHORIZED)
            return Response({"message": "Connexion réussie", "id": user.id, "role": role}, status=status.HTTP_200_OK)
        except model.DoesNotExist:
            return Response({"detail": "Utilisateur non trouvé."}, status=status.HTTP_404_NOT_FOUND)


class ListePresencesAPIView(APIView):
    def get(self, request, cours_id):
        try:
            presences = Attendance.objects.filter(cours_id=cours_id).select_related('etudiant')
            serializer = AttendanceSerializer(presences, many=True)
            return Response(serializer.data)
        except Cours.DoesNotExist:
            return Response({"detail": "Cours introuvable."}, status=status.HTTP_404_NOT_FOUND)
        

from rest_framework.views import APIView

class SoumettrePresencesAPIView(APIView):
    def post(self, request):
        data = request.data
        if not isinstance(data, list):
            return Response({"detail": "Données invalides"}, status=status.HTTP_400_BAD_REQUEST)
        
        for item in data:
            attendance_id = item.get("id")
            statut = item.get("statut")
            if attendance_id is not None and statut in ['present', 'absent', 'en_retard']:
                Attendance.objects.filter(id=attendance_id).update(statut=statut)

        return Response({"message": "Présences mises à jour avec succès."})


#logout view 
