package com.oscarcreator.opengl_game.renderEngine;


import android.content.Context;

import com.oscarcreator.opengl_game.entities.Entity;
import com.oscarcreator.opengl_game.library.Matrix4f;
import com.oscarcreator.opengl_game.models.RawModel;
import com.oscarcreator.opengl_game.models.TexturedModel;
import com.oscarcreator.opengl_game.shaders.StaticShader;
import com.oscarcreator.opengl_game.textures.ModelTexture;
import com.oscarcreator.opengl_game.toolbox.Maths;
import com.oscarcreator.opengl_game.util.Constants;

import java.util.List;
import java.util.Map;

import static android.opengl.GLES20.*;
import static android.opengl.GLES30.*;

import static com.oscarcreator.opengl_game.util.Constants.*;

public class Renderer {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000f;


	private Matrix4f projectionMatrix;
	private StaticShader shader;

	private int width, height;

	public Renderer(int width, int height, StaticShader shader){
		this.width = width;
		this.height = height;
		this.shader = shader;

		// disables rendering of backfaces(inside the model)
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		createProjectionMatrix();
		shader.useProgram();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void prepare(){
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.5f,1f, 1f, 1);
	}


	public void render(Map<TexturedModel, List<Entity>> entities){
		for (TexturedModel model : entities.keySet()){
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch){
				prepareInstances(entity);

				glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(),
						GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}

	}

	private void prepareTexturedModel(TexturedModel model){
		RawModel rawModel = model.getRawModel();

		glBindVertexArray(rawModel.getVaoID());
		//Enabling attribarray at the position 0 which we put the model into
		glEnableVertexAttribArray(POSITION_VBO_LOCATION);

		glEnableVertexAttribArray(TEXTURE_VBO_LOCATION);
		glEnableVertexAttribArray(NORMALS_VBO_LOCATION);

		ModelTexture texture = model.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

		//sampler2d uses this texturebank as the default
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, model.getTexture().getID());

	}

	private void unbindTexturedModel(){
		glDisableVertexAttribArray(POSITION_VBO_LOCATION);
		glDisableVertexAttribArray(TEXTURE_VBO_LOCATION);
		glDisableVertexAttribArray(NORMALS_VBO_LOCATION);

		glBindVertexArray(0);
	}

	private void prepareInstances(Entity entity){
		Matrix4f transformationMatrix = Maths.creatTransformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}




	private void createProjectionMatrix(){
		float aspectRatio = (float) width / (float) height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;

	}

}
