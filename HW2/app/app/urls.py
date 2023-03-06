"""app URL Configuration"""

from django.urls import re_path, include
from rest_framework.routers import SimpleRouter

from app.src.views import MyViewSet
r = SimpleRouter()
r.register('', MyViewSet, basename='myrouter')
urlpatterns = r.urls
