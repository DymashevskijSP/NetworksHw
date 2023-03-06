from rest_framework.decorators import action
from rest_framework.viewsets import ViewSet
from rest_framework.response import Response
from app.src.models.models import Product
from django.http import HttpResponse


class MyViewSet(ViewSet):
    def list(self, request):
        if request.query_params.get('id') is not None:
            id = int(request.query_params.get('id'))
            product = Product.objects.all().filter(id=id).first()
            if product is None:
                return Response({"ok": False, "reason": "product not found"}, status=404)
            return Response({"ok": True, "product": {"description": product.description, "id": product.id}})
        return Response({"products": [{"id": x.id, "description": x.description, "name": x.name} for x in
                                      Product.objects.all()]})

    def retrieve(self, request, pk=None):
        id = int(pk)
        product = Product.objects.all().filter(id=id).first()
        if product is None:
            return Response({"ok": False, "reason": "product not found"}, status=404)
        response = Response(
            {"ok": True, "product": {"id": product.id, "description": product.description, "name": product.name}})
        return response

    def destroy(self, request, pk=None):
        id = int(pk)
        product = Product.objects.all().filter(id=id).first()
        if product is None:
            return Response({"ok": False, "reason": "product not found"}, status=404)
        product.delete()
        return Response({"ok": True})

    def create(self, request):
        description = request.data.get('description')
        name = request.data.get('name')
        if description and name is None:
            return Response({"ok": False, "reason": f"product should have description and name {name} {description}"}, status=400)
        image = request.data.get("image")
        p = Product(description=description, image=image, name=name)
        p.save()
        assert p.image is not None
        return Response({"ok": True, "id": p.id})

    def partial_update(self, request, pk=None):
        id = int(pk)
        product = Product.objects.all().filter(id=id).first()
        if product is None:
            return Response({"ok": False, "reason": "product not found"}, status=404)
        data = request.data
        description = data.get('description')
        image = data.get('image')
        if (description):
            product.description = description
        if image:
            product.image = image
        product.save()
        return Response({"ok": True})

    def update(self, request, pk=None):
        id = int(pk)
        product = Product.objects.all().filter(id=id).first()
        if product is None:
            return Response({"ok": False, "reason": "product not found"}, status=404)
        data = request.data
        description = data.get('description')
        image = data.get('image')
        name = data.get('name')
        if not description and image and name:
            return Response({"ok": False, "reason": "all fields should be changed"}, status=404)
        product.description = description
        product.image = image
        product.save()
        return Response({"ok": True})

    @action(url_path='images', methods=['get'], detail=True)
    def get_image(self, request, pk=None):
        id = int(pk)
        product = Product.objects.all().filter(id=id).first()
        if product is None:
            return Response({"ok": False, "reason": "product not found"})
        if product.image:
            return HttpResponse(product.image, content_type='image/jpeg')
        else:
            return Response({"ok": False, "reason": "product have no picture"})