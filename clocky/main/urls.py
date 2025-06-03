from django.urls import path
from .views import (
    EtudiantCreateAPIView,
    ProfesseurCreateAPIView,
    CoursCreateAPIView,
    AttendanceCreateAPIView,
    ListePresencesAPIView,
    SoumettrePresencesAPIView,
    EtudiantLoginView,
    ProfesseurLoginView,
)

urlpatterns = [
    
    path('api/etudiants/', EtudiantCreateAPIView.as_view(), name='api_etudiant_create'),
    path('api/professeurs/', ProfesseurCreateAPIView.as_view(), name='api_professeur_create'),
    

    
    path('api/cours/', CoursCreateAPIView.as_view(), name='api_cours_create'),

    
    path('api/attendance/', AttendanceCreateAPIView.as_view(), name='api_attendance_create'),

    
    path('api/cours/<int:cours_id>/presences/', ListePresencesAPIView.as_view(), name='api_presences_par_cours'),

    
    path('api/presences/soumettre/', SoumettrePresencesAPIView.as_view(), name='api_presences_soumettre'),
    path('login/etudiant/', EtudiantLoginView.as_view(), name='login_etudiant'),
    path('login/professeur/', ProfesseurLoginView.as_view(), name='login_professeur'),
]
