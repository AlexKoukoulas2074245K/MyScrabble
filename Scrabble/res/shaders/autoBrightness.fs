uniform sampler2D myTexture;
uniform float brightnessFactor;
varying vec2 vTexCoord;

void main()
{
	gl_FragColor = texture2D(myTexture, vTexCoord).rgba * vec4(brightnessFactor, brightnessFactor, brightnessFactor, 1.0);
}