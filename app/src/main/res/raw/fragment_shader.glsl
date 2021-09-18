precision mediump float;

//in - varying
varying vec2 pass_textureCoords;
varying vec3 surfaceNormal;
varying vec3 toLightVector;
varying vec3 toCameraVector;

uniform sampler2D textureSampler;
uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;

void main(void){

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLightVector);
	float nDot1 = dot(unitNormal, unitLightVector);

	float brightness = max(nDot1, 0.15);
	vec3 diffuse = brightness * lightColour;


	vec3 unitVectorToCamera = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

	float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
	specularFactor = max(specularFactor, 0.0);
	float dampedFactor = pow(specularFactor, shineDamper);
	vec3 finalSpecular = dampedFactor * reflectivity * lightColour;

	gl_FragColor = vec4(diffuse, 1.0) * texture2D(textureSampler, pass_textureCoords) + vec4(finalSpecular, 1.0);

}