from django.db import models

class Product(models.Model):
    id = models.BigAutoField(primary_key=True)
    description = models.CharField(max_length=500, null=False)
    name = models.CharField(max_length=50, null=False)
    image = models.ImageField(default='app/src/images/defaultimage.jpg', upload_to='app/src/images/', null=False, blank=False)
