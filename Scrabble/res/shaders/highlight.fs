uniform sampler2D myTexture;
varying vec2 vTexCoord;

vec4 standardBrightness = vec4(1.3, 1.3, 1.3, 1);

void main()
{
	gl_FragColor = texture2D(myTexture, vTexCoord).rgba * standardBrightness;
}