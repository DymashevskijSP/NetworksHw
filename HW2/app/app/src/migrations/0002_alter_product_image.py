# Generated by Django 4.1.2 on 2023-03-04 18:07

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('src', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='product',
            name='image',
            field=models.ImageField(default='app/src/images/default.jpg', upload_to='app/src/images/'),
        ),
    ]
