uniform sampler2D myTexture;
uniform vec3 darknessFactor;
varying vec2 vTexCoord;

void main()
{
	gl_FragColor = texture2D(myTexture, vTexCoord).rgba * vec4(darknessFactor, 1);
}