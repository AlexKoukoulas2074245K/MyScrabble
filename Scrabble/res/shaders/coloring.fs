uniform vec3 inputColor;
uniform vec3 darknessFactor;

void main()
{
	gl_FragColor = vec4(inputColor, 1) * vec4(darknessFactor, 1);
}