uniform sampler2D myTexture;
uniform vec3 darknessParam;
varying vec2 vTexCoord;

void main()
{
	gl_FragColor = texture2D(myTexture, vTexCoord).rgba * vec4(darknessParam, 1);
}