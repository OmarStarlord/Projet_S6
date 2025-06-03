from rest_framework import generics, status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework_simplejwt.views import TokenObtainPairView

from .models import User, Etudiant, Cours, Attendance
from .serializers import (
    EtudiantCreateSerializer,
    CoursSerializer,
    AttendanceSerializer,
    CustomTokenObtainPairSerializer,
)

# Auth avec JWT
class CustomTokenObtainPairView(TokenObtainPairView):
    serializer_class = CustomTokenObtainPairSerializer

# Créer un étudiant (avec User)
class EtudiantCreateAPIView(generics.CreateAPIView):
    queryset = Etudiant.objects.all()
    serializer_class = EtudiantCreateSerializer

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


