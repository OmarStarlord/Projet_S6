from django.urls import path
from .views import (
    EtudiantCreateAPIView,
    ProfesseurCreateAPIView,
    AdminCreateAPIView,
    CoursCreateAPIView,
    AttendanceCreateAPIView,
)

urlpatterns = [
    path('api/etudiants/', EtudiantCreateAPIView.as_view(), name='api_etudiant_create'),
    path('api/professeurs/', ProfesseurCreateAPIView.as_view(), name='api_professeur_create'),
    path('api/admins/', AdminCreateAPIView.as_view(), name='api_admin_create'),
    path('api/cours/', CoursCreateAPIView.as_view(), name='api_cours_create'),
    path('api/attendance/', AttendanceCreateAPIView.as_view(), name='api_attendance_create'),
]
