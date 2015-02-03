uniform sampler2D myTexture;
uniform vec3 darknessFactor;
uniform int highlighted;

varying vec2 vTexCoord; 
vec4 standardBrightness = vec4(1.3, 1.3, 1.3, 1);

void main()
{
    gl_FragColor = texture2D(myTexture, vTexCoord).rgba * vec4(darknessFactor, 1);
    
    if(highlighted == 1)
    {
	   gl_FragColor = gl_FragColor * standardBrightness;
    }
}