package com.oscarcreator.opengl_game.renderEngine;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.oscarcreator.opengl_game.models.RawModel;
import com.oscarcreator.opengl_game.util.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


import static android.opengl.GLES30.*;
import static android.opengl.GLUtils.texImage2D;
import static com.oscarcreator.opengl_game.util.Constants.NORMALS_VBO_LOCATION;
import static com.oscarcreator.opengl_game.util.Constants.POSITION_VBO_LOCATION;
import static com.oscarcreator.opengl_game.util.Constants.TEXTURE_VBO_LOCATION;

public class Loader {

	private static final String TAG = "Loader";

	private List<Integer> vaos = new ArrayList<>();
	private List<Integer> vbos = new ArrayList<>();

	private List<Integer> textures = new ArrayList<>();

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);

		storeDataInAttributeList(POSITION_VBO_LOCATION,3, positions);
		storeDataInAttributeList(TEXTURE_VBO_LOCATION, 2, textureCoords);
		storeDataInAttributeList(NORMALS_VBO_LOCATION, 3, normals);

		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	//TODO lookup
	/*
	public int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",
					new FileInputStream("res/" + fileName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureId = texture.getTextureID();
		textures.add(textureId);
		return textureId;

	}
	*/

	//Takes a context and resourceid and returns the id of the loaded texture.
	public int loadTexture(Context context, int resourceId){


		final int[] textureObjectIds = new int[1];
		//Generate a new texture object
		glGenTextures(1, textureObjectIds, 0);

		//Checks generation succeeded
		if (textureObjectIds[0] == 0){
			if (Constants.LOGGING){
				Log.i(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}

		//Create a bitmapfactory
		final BitmapFactory.Options options = new BitmapFactory.Options();
		//Tell's android that original image data instead of a scaled version.
		options.inScaled = false;
		//Get the data by decoding the image to a bitmap
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

		if (bitmap == null){
			if (Constants.LOGGING){
				Log.i(TAG,"Resource ID " + resourceId + " could not be decoded.");
			}
			glDeleteTextures(1,textureObjectIds,0);
			return 0;
		}
		//future calls should be applied to this texture object.
		//1:treated as a two-dim texture, 2:which texture object to bind.
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

		//For minification use trilinear filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		//For magnification use bilinear filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		//tells OpenGL to read in the bitmap data defined by bitmap and copy it
		// over into the texture object that is currently bound
		texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		//speed up garbage collection by releasing data immediately
		bitmap.recycle();
		//Generate all different levels of mipmaps for the texture
		glGenerateMipmap(GL_TEXTURE_2D);
		//unbinds the last binded texture
		glBindTexture(GL_TEXTURE_2D,0);

		textures.add(textureObjectIds[0]);

		return textureObjectIds[0];

	}


	/**Removes all the vaos, vbos*/
	public void cleanUp(){

		int[] temp = new int[1];
		for (int vao : vaos){
			temp[0] = vao;
			glDeleteVertexArrays(1, temp, 0);
		}
		for (int vbo : vbos){
			temp[0] = vbo;
			glDeleteBuffers(1, temp, 0);
		}

		for (int texture : textures){
			temp[0] = texture;
			glDeleteTextures(1, temp, 0);
		}

	}

	private int createVAO(){
		//TODO check
		int[] vaoID = new int[1];
		glGenVertexArrays(1, vaoID, 0);
		vaos.add(vaoID[0]);
		glBindVertexArray(vaoID[0]);
		return vaoID[0];
	}

	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int[] vboID = new int[1];
		glGenBuffers(1, vboID, 0);
		vbos.add(vboID[0]);
		glBindBuffer(GL_ARRAY_BUFFER, vboID[0]);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		//TODO check
		glBufferData(GL_ARRAY_BUFFER, data.length * 4, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
		//Unbinds the current vb0
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO(){
		glBindVertexArray(0);

	}

	private void bindIndicesBuffer(int[] indices){
		int[] vboID = new int[1];
		//TODO check
		glGenBuffers(1, vboID, 0);
		vbos.add(vboID[0]);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID[0]);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		//GL_STATIC_DRAW = data will never be changed
		//TODO check
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices.length * 4, buffer, GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data){
		//IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data){
		//FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		FloatBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		buffer.put(data);
		buffer.flip();

		return buffer;

	}

}

