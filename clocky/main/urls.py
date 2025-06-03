from django.urls import path
from .views import (
    EtudiantCreateAPIView,
    ProfesseurCreateAPIView,
    AdminCreateAPIView,
    CoursCreateAPIView,
    AttendanceCreateAPIView,
    LoginAPIView,
    ListePresencesAPIView,
    SoumettrePresencesAPIView,
)

urlpatterns = [
    # Création des utilisateurs
    path('etudiants/', EtudiantCreateAPIView.as_view(), name='api_etudiant_create'),
    path('professeurs/', ProfesseurCreateAPIView.as_view(), name='api_professeur_create'),
    path('admins/', AdminCreateAPIView.as_view(), name='api_admin_create'),

    # Création d'un cours
    path('cours/', CoursCreateAPIView.as_view(), name='api_cours_create'),

    # Création manuelle de présence (optionnel)
    path('attendance/', AttendanceCreateAPIView.as_view(), name='api_attendance_create'),

    # Authentification
    path('login/', LoginAPIView.as_view(), name='api_login'),

    # Présences pour un cours spécifique
    path('cours/<int:cours_id>/presences/', ListePresencesAPIView.as_view(), name='api_presences_par_cours'),

    # Soumission des présences
    path('presences/soumettre/', SoumettrePresencesAPIView.as_view(), name='api_presences_soumettre'),
    path('token/', CustomTokenObtainPairView.as_view(), name='token_obtain_pair'),
]
