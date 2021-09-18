package com.oscarcreator.opengl_game.renderEngine;

import android.content.Context;

import com.oscarcreator.opengl_game.entities.Camera;
import com.oscarcreator.opengl_game.entities.Entity;
import com.oscarcreator.opengl_game.entities.Light;
import com.oscarcreator.opengl_game.models.TexturedModel;
import com.oscarcreator.opengl_game.shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private StaticShader shader;
    private Renderer renderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public MasterRenderer(int width, int height, Context context){
        shader = new StaticShader(context);
        renderer = new Renderer(width, height, shader);
    }

    public void render(Light sun, Camera camera){
        renderer.prepare();
        shader.useProgram();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);

        renderer.render(entities);

        shader.stop();
        entities.clear();
    }

    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void cleanUp(){
        shader.cleanUp();
    }


}
