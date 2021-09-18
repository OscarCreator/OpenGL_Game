package com.oscarcreator.opengl_game.shaders;


import android.content.Context;
import android.util.Log;

import com.oscarcreator.opengl_game.library.BufferUtils;
import com.oscarcreator.opengl_game.library.Matrix4f;
import com.oscarcreator.opengl_game.util.LoggerConfig;
import com.oscarcreator.opengl_game.library.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

public abstract class ShaderProgram {

	private static final String TAG = "ShaderProgram";

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(Context context, int vertexShaderResourceId,
						 int fragmentShaderResourceId){
		vertexShaderID = loadShader(context, vertexShaderResourceId, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(context, fragmentShaderResourceId, GL_FRAGMENT_SHADER);
		//Program
		programID = glCreateProgram();
		//Attaching vertex and fragmentshader to the program
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);

		bindAttributes();
		glLinkProgram(programID);
		glValidateProgram(programID);

		getAllUniformLocations();
	}

	protected abstract void getAllUniformLocations();

	protected int getUniformLocation(String uniformName){
		return glGetUniformLocation(programID, uniformName);
	}

	public void useProgram(){
		glUseProgram(programID);
	}

	public void stop(){
		glUseProgram(0);
	}

	public void cleanUp(){
		stop();
		//detach shaders from program
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		//Delete shaders
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		//Delete program
		glDeleteProgram(programID);
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName){
		glBindAttribLocation(programID, attribute, variableName);
	}

	protected void loadFloat(int location, float value){
		glUniform1f(location, value);
	}


	protected void loadVector(int location, Vector3f vector){
		glUniform3f(location, vector.x, vector.y, vector.z);
	}


	protected void loadBoolean(int location, boolean value){
		float toLoad = 0;
		if (value){
			toLoad = 1;
		}
		glUniform1f(location, toLoad);
	}


	protected void loadMatrix(int location, Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(location, 1, false, matrixBuffer);
	}

	private static int loadShader(Context context, int resourceId, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			InputStream inputStream = context.getResources().openRawResource(resourceId);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line;
			while((line = reader.readLine()) != null){
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}

		int shaderID = glCreateShader(type);

		//Uploads the shader code to the shaderObject
		glShaderSource(shaderID, shaderSource.toString());

		//Compiles the shader code which is in the shaderObject
		glCompileShader(shaderID);

		//Retrieves information if the shader compilation succeeded.
		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderID, GL_COMPILE_STATUS, compileStatus, 0);

		//Log the shader result
		if (LoggerConfig.ON) {
			Log.v(TAG, "Result of compiling source:\n" + shaderSource.toString() + "\n:"
					+ glGetShaderInfoLog(shaderID));
		}

		//Verifying if the shader compilation was successful.
		if (compileStatus[0] == 0) {
			//Deletes the shader object if compilation failed.
			glDeleteShader(shaderID);

			if (LoggerConfig.ON) {
				Log.w(TAG, "compilation of shader failed.");
			}
			return 0;
		}

		return shaderID;
	}

}