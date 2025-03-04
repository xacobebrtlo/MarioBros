package com.xacobe.mario.Tools;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;
import com.xacobe.mario.Sprites.Cofres;
import com.xacobe.mario.Sprites.Demon;
import com.xacobe.mario.Sprites.NoShurikenDude;

public class B2WorldCreator {
    public Array<NoShurikenDude> getNoshurikenDUdes() {
        return noshurikenDUdes;
    }
    public Array<Demon> getDemons() {
        return demons;
    }

    private Array<NoShurikenDude> noshurikenDUdes;
    private Array<Demon> demons;


    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fded = new FixtureDef();
        Body body;

//        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
//            Rectangle rec = ((RectangleMapObject) object).getRectangle();
//            bdef.type = BodyDef.BodyType.StaticBody;
//            bdef.position.set((rec.getX() + rec.getWidth() / 2) / MarioBros.PPM, (rec.getY() + rec.getHeight() / 2) / MarioBros.PPM);
//            fded.isSensor = true;
//
//            body = world.createBody(bdef);
//            shape.setAsBox(rec.getWidth() / 2 / MarioBros.PPM, rec.getHeight() / 2 / MarioBros.PPM);
//            fded.shape = shape;
//            body.createFixture(fded);
//            fded.filter.categoryBits = MarioBros.GROUND_BIT;
//        }


        //Suelo Rectangulos
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rec = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rec.getX() + rec.getWidth() / 2) / MarioBros.PPM, (rec.getY() + rec.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rec.getWidth() / 2 / MarioBros.PPM, rec.getHeight() / 2 / MarioBros.PPM);
            fded.shape = shape;
            body.createFixture(fded);
            fded.filter.categoryBits = MarioBros.GROUND_BIT;
        }

        //Suelo Rampa Poligono
        //polygon
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(PolygonMapObject.class)) {
            Polygon polygon = ((PolygonMapObject) object).getPolygon();
            BodyDef bodyDef = new BodyDef();
            FixtureDef fixtureDef = new FixtureDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(polygon.getOriginX(), polygon.getOriginY());
            Body bodyPol = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            fixtureDef.shape = convertPolygonToPolygonShape(polygon, MarioBros.PPM);
            fixtureDef.friction = 0f;
            bodyPol.createFixture(fixtureDef);
            fixtureDef.filter.categoryBits = MarioBros.GROUND_BIT;
        }

        //Crear todos los NoshurikenDude
        noshurikenDUdes = new Array<NoShurikenDude>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rec = ((RectangleMapObject) object).getRectangle();
            noshurikenDUdes.add(new NoShurikenDude(screen, rec.getX() / MarioBros.PPM, rec.getY() / MarioBros.PPM));
        }

        //Crear los cofres
        MapLayer cofreLayer = screen.getMap().getLayers().get(8); // La capa 8
        for (MapObject object : cofreLayer.getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            // Creamos un cofre en la posición del objeto, escalando a PPM:
            new Cofres(screen, rect.x / MarioBros.PPM, rect.y / MarioBros.PPM, rect.width / MarioBros.PPM, rect.height / MarioBros.PPM);


        }

        // Crea todos los DEMON desde la capa correspondiente
        demons = new Array<Demon>();
        {
            // Ajusta el índice de la capa según tu Tiled. Por ejemplo, si la capa se llama "Demon" y es la 10ª capa, su índice podría ser 9.
            MapLayer demonLayer = screen.getMap().getLayers().get(9);
            for (MapObject object : demonLayer.getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                // Crea el Demon y lo añade al array
                demons.add(new Demon(screen, rect.x / MarioBros.PPM, rect.y / MarioBros.PPM));
            }
        }


    }

    // chatgpt (revisar y entender)
    public PolygonShape convertPolygonToPolygonShape(Polygon polygon, float pixelsPerMeter) {
        PolygonShape polygonShape = new PolygonShape();

        // Get the transformed vertices from the LibGDX Polygon
        float[] vertices = polygon.getTransformedVertices();

        // Convert the vertices to Box2D's format (scaled to meters)
        Vector2[] box2dVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < vertices.length / 2; i++) {
            float x = vertices[i * 2] / pixelsPerMeter;
            float y = vertices[i * 2 + 1] / pixelsPerMeter;
            box2dVertices[i] = new Vector2(x, y);
        }

        // Ensure the vertices form a convex polygon (Box2D requires this)
        if (box2dVertices.length <= 8) { // Box2D supports up to 8 vertices
            float[] convexVertices = new float[box2dVertices.length * 2];
            for (int i = 0; i < box2dVertices.length; i++) {
                convexVertices[i * 2] = box2dVertices[i].x;
                convexVertices[i * 2 + 1] = box2dVertices[i].y;
            }
            polygonShape.set(convexVertices);
        } else {
            throw new IllegalArgumentException("Polygon has too many vertices for Box2D (max 8).");
        }

        return polygonShape;
    }
}

